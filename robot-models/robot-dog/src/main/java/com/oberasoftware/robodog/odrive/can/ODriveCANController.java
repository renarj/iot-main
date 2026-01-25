package com.oberasoftware.robodog.odrive.can;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.oberasoftware.base.event.EventBus;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.robodog.odrive.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ODriveCANController implements ODriveInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ODriveCANController.class);

    private final CANConnector canConnector;
    private final Multimap<Integer, ODriveEventListener> eventListeners = Multimaps.newListMultimap(new HashMap<>(), java.util.ArrayList::new);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean open = new AtomicBoolean(false);
    private final EventBus eventBus;

    public ODriveCANController(CANConnector canConnector, EventBus eventBus) {
        this.canConnector = canConnector;
        this.eventBus = eventBus;
    }

    @Override
    public void open() {
        canConnector.open();
        open.set(true);

        executorService.submit(this::listenForOdriveMessages);
    }

    @Override
    public void close() {
        LOG.info("Closing ODrive CAN controller");
        open.set(false);

        executorService.close();
        canConnector.close();
    }

    private void listenForOdriveMessages() {
        LOG.info("Listening for ODrive messages");
        while (Thread.currentThread().isAlive() && open.get()) {
            try {
                CanFrame f = canConnector.receive();
                int arbId = f.getId();
                int nodeId = arbId >> 5;
                int cmdId  = arbId & 0x1F;
                if(!eventListeners.containsKey(cmdId)) {
                    LOG.warn("Received unhandled command: {} from node: {} frame: {}", String.format("%02X", cmdId), nodeId, f);
                } else {
                    eventListeners.get(cmdId).forEach(l -> {
                        if (l != null) {
                            l.onEvent(nodeId, cmdId, f);
                        }
                    });
                }
            } catch (Exception e) {
                LOG.error("Error reading from channel", e);
            }
        }
        LOG.info("Thread was interrupted or channel was closed");
    }

    @Override
    public void discoverDrives() {
        ensureOpen();
        CanFrame frame = ODriveCANFrameUtil.discoverFrame();
        canConnector.send(frame);
    }

    @Override
    public void setAxisState(int nodeId, ODriveState state) {
        ensureOpen();
        CanFrame frame = ODriveCANFrameUtil.setAxisState(nodeId, state.getCode());
        canConnector.send(frame);
    }

    @Override
    public void setControllerModes(int nodeId, ControlMode controlMode, InputMode inputMode) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setControllerModes(nodeId, controlMode, inputMode));
    }

    @Override
    public void setInputPosition(int nodeId, float turns) {
        ensureOpen();
        LOG.info("Sending position command to node: {} turns: {}", nodeId, turns);
        canConnector.send(ODriveCANFrameUtil.setInputPosition(nodeId, turns, 0, 0));
    }

    @Override
    public void setInputVelocity(int nodeId, float vel) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setInputVelocity(nodeId, vel, 0));
    }

    @Override
    public void setLimits(int nodeId, float velLimit, float current) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setLimits(nodeId, velLimit, current));
    }

    @Override
    public void setTrajectorLimit(int nodeId, float velLimit) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setTrajLimits(nodeId, velLimit));
    }

    @Override
    public void rebootDrive(int nodeId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.rebootDrive(nodeId));
    }

    @Override
    public void requestCurrentEstimate(int nodeId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.requestIq(nodeId));
    }

    @Override
    public void requestEncoderEstimates(int nodeId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.requestEncoderEstimates(nodeId));
    }

    @Override
    public void readParameter(int nodeId, int endpointId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.sendReadRXCommand(nodeId, endpointId));
    }

    @Override
    public void writeParameter(int nodeId, int endpointId, float value) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.sendWriteRXCommand(nodeId, endpointId, value));
    }

    @Override
    public void requestBusVoltage(int nodeId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.requestBusVoltage(nodeId));
    }

    @Override
    public void requestTemperature(int nodeId) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.requestTemperature(nodeId));
    }

    @Override
    public void setPositionGain(int nodeId, float gain) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setPositionGain(nodeId, gain));
    }

    @Override
    public void setVelocityGain(int nodeId, float gain, float integratorGain) {
        ensureOpen();
        canConnector.send(ODriveCANFrameUtil.setVelocityGain(nodeId, gain, integratorGain));
    }

    private void ensureOpen() {
        if (!canConnector.isOpen()) {
            canConnector.open();
        }
    }

    @Override
    public void registerListener(ODriveEventListener listener) {
        this.eventListeners.put(listener.getCommandId().getValue(), listener);
        if(listener instanceof EventHandler) {
            eventBus.registerHandler((EventHandler) listener);
        }
    }

    @Override
    public void removeListener(ODriveEventListener listener) {
        this.eventListeners.remove(listener.getCommandId().getValue(), listener);
    }
}
