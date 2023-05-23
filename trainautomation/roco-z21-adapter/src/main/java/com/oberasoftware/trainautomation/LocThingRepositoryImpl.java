package com.oberasoftware.trainautomation;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.train.TrainConstants;
import com.oberasoftware.iot.core.train.model.Locomotive;
import com.oberasoftware.iot.core.util.IntUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LocThingRepositoryImpl implements LocThingRepository {
    private static final Logger LOG = LoggerFactory.getLogger(LocThingRepositoryImpl.class);

    private static final int SYNC_INTERVAL = 60000;

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private AgentControllerInformation controllerInformation;

    private final Lock lock = new ReentrantLock();

    private final ListMultimap<String, IotThing> mappedThings = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void startSync() {
        LOG.info("Scheduling thing sync service for locomotives and accessories");
        executorService.submit(new LocSyncThread());
    }

    @Override
    public List<IotThing> getLocomotiveForLocAddress(String controllerId, int locAddress) {
        lock.lock();
        try {
            return mappedThings.get(getKey(controllerId, locAddress));
        } finally {
            lock.unlock();
        }
    }

    private class LocSyncThread implements Runnable {
        @Override
        public void run() {
            LOG.info("Started locomotive sync service");
            while(!Thread.interrupted()) {
                try {
                    var things = thingClient.getThings(controllerInformation.getControllerId(), TrainConstants.EXTENSION_ID);
                    LOG.info("Syncing: {} things from remote", things);
                    lock.lock();
                    try {
                        mappedThings.clear();
                        things.forEach(this::mapIfPresent);
                    } finally {
                        lock.unlock();
                    }
                } catch (IOTException e) {
                    LOG.error("Could not retrieve remote locomotives", e);
                } catch(Exception e) {
                    LOG.error("", e);
                }

                LOG.debug("Sleep, waiting for next sync");
                Uninterruptibles.sleepUninterruptibly(SYNC_INTERVAL, TimeUnit.MILLISECONDS);
            }
            LOG.info("Locomotive sync service is stopped");
        }

        private void mapIfPresent(IotThing t) {
            if(t.getProperties().containsKey(Locomotive.LOC_ADDRESS)) {
                var oLocAddress = IntUtils.toInt(t.getProperty(Locomotive.LOC_ADDRESS));
                LOG.info("Mapping thing: {} to locomotives map with loc address: {}", t, oLocAddress);
                oLocAddress.ifPresent(la -> mappedThings.put(getKey(t.getControllerId(), la), t));
            }
        }
    }

    private static String getKey(String controllerId, int locAddress) {
        return controllerId + "-" + locAddress;
    }
}
