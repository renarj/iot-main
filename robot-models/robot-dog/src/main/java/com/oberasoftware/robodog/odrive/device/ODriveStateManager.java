package com.oberasoftware.robodog.odrive.device;

import com.oberasoftware.base.event.EventBus;
import com.oberasoftware.robodog.odrive.exception.ODriveException;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestCurrentCommand;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestPositionCommand;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestTemperatureCommand;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestVoltageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Central in-memory state manager for ODrive devices.
 *
 * Maintains a thread-safe per-node attribute store and can:
 * - Run a background poller that periodically requests current estimates
 * - Perform a blocking request for an attribute value with a timeout, inspired by DynamixelServoDataManager
 */
public class ODriveStateManager {
    private static final Logger LOG = LoggerFactory.getLogger(ODriveStateManager.class);

    private final ConcurrentHashMap<Integer, NodeStateHolder> nodeState = new ConcurrentHashMap<>();

    private static final long DEFAULT_TIMEOUT = 1000L;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pollingTask;

    private final EventBus eventBus;

    public ODriveStateManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Registers a node ID so that it is included in polling and state storage.
     */
    public void registerNode(int nodeId) {
        nodeState.computeIfAbsent(nodeId, NodeStateHolder::new);
    }

    /**
     * Removes a node and its state.
     */
    public void removeNode(int nodeId) {
        nodeState.remove(nodeId);
    }

    /**
     * @return Copy of known node IDs.
     */
    public Set<Integer> getKnownNodeIds() {
        return Collections.unmodifiableSet(nodeState.keySet());
    }

    /**
     * Stores an attribute value for the given node and signals any waiters.
     */
    public void setAttribute(int nodeId, ODriveAttribute attribute, double value) {
        registerNode(nodeId);
        NodeStateHolder holder = nodeState.get(nodeId);
        holder.putData(attribute, value);
        holder.signal();
    }


    public Double getAttribute(int nodeId, ODriveAttribute attribute) {
        return getAttribute(nodeId, attribute, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    /**
     * Retrieves an attribute value for the given node.
     */
    public Double getAttribute(int nodeId, ODriveAttribute attribute, long timeout, TimeUnit unit) {
        NodeStateHolder holder = nodeState.get(nodeId);

        LOG.info("Requesting attribute update for node: {} attribute: {}", nodeId, attribute);
        switch(attribute) {
            case CURRENT_TARGET, CURRENT:
                eventBus.publish(new RequestCurrentCommand(nodeId));
                break;
            case BUS_VOLTAGE, BUS_CURRENT:
                eventBus.publish(new RequestVoltageCommand(nodeId));
                break;
            case BOARD_TEMPERATURE, MOTOR_TEMPERATURE:
                eventBus.publish(new RequestTemperatureCommand(nodeId));
                break;
            case POSITION:
                eventBus.publish(new RequestPositionCommand(nodeId));
                break;
            default:
                throw new ODriveException("Unsupported attribute: " + attribute);
        }

        LOG.debug("Waiting for attribute update for node: {} attribute: {}", nodeId, attribute);
        holder.waitForUpdate(timeout, unit);

        LOG.debug("Got attribute update for node: {} attribute: {}", nodeId, attribute);
        return holder.getValue(attribute);
    }

    /**
     * Retrieves the full attribute map for a node.
     */
    public Map<ODriveAttribute, Double> getOdriveState(int nodeId) {
        NodeStateHolder holder = nodeState.get(nodeId);
        return holder != null ? holder.getValues() : Collections.emptyMap();
    }

    /**
     * Checks whether an attribute has been updated within the provided freshness window.
     */
    public boolean hasFreshAttribute(int nodeId, ODriveAttribute attribute, long freshnessMs) {
        NodeStateHolder holder = nodeState.get(nodeId);
        return holder != null && holder.isUpdatedInTimeFrame(attribute, freshnessMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the background polling task if running.
     */
    public synchronized void stopPolling() {
        if (pollingTask != null) {
            pollingTask.cancel(false);
            pollingTask = null;
        }
    }

    /**
     * Shuts down scheduler. After calling this, the instance should not be reused for polling.
     */
    public synchronized void shutdown() {
        stopPolling();
        scheduler.shutdownNow();
    }

    private long getDefaultIntervalMs() {
        String fromProp = System.getProperty("odrive.poll.interval.ms", System.getenv("ODRIVE_POLL_INTERVAL_MS"));
        if (fromProp != null) {
            try {
                return Long.parseLong(fromProp);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid poll interval provided: {}", fromProp);
            }
        }
        return 1000L; // default 1s
    }

    private static class NodeStateHolder {
        private final Map<ODriveAttribute, Double> values = new ConcurrentHashMap<>();
        private final Map<ODriveAttribute, Long> updateTimes = new ConcurrentHashMap<>();

        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();

        private final int nodeId;

        private NodeStateHolder(int nodeId) {
            this.nodeId = nodeId;
        }

        private Map<ODriveAttribute, Double> getValues() {
            return Map.copyOf(values);
        }

        private Double getValue(ODriveAttribute attribute) {
            return values.get(attribute);
        }

        private boolean isUpdatedInTimeFrame(ODriveAttribute attribute, long time, TimeUnit unit) {
            Long updateTime = updateTimes.get(attribute);
            if (updateTime == null) return false;

            long millisSinceUpdate = System.currentTimeMillis() - updateTime;
            return millisSinceUpdate < TimeUnit.MILLISECONDS.convert(time, unit);
        }

        private void putData(ODriveAttribute attribute, Double value) {
            values.put(attribute, value);
            updateTimes.put(attribute, System.currentTimeMillis());
        }

        private void waitForUpdate(long time, TimeUnit unit) {
            lock.lock();
            try {
                boolean found = condition.await(time, unit);
                if(!found) {
                    LOG.error("Did not get a ODrive update for node: {}", nodeId);
                    throw new ODriveException("Could not read ODrive data for node: " + nodeId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.info("Wait for update interrupted for node {}: {}", nodeId, e.getMessage());
            } finally {
                lock.unlock();
            }
        }

        private void signal() {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
