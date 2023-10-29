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

    private final ListMultimap<String, IotThing> mappedLocs = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    private final ListMultimap<String, IotThing> mappedSensors = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void startSync() {
        LOG.info("Scheduling thing sync service for locomotives and accessories");
        executorService.submit(new LocSyncThread());
    }

    @Override
    public List<IotThing> getLocomotiveForLocAddress(String controllerId, int locAddress) {
        return filter(mappedLocs, controllerId, locAddress);
    }

    @Override
    public List<IotThing> getSensors(String controllerId, int sensorPort) {
        return filter(mappedSensors, controllerId, sensorPort);
    }

    private List<IotThing> filter(ListMultimap<String, IotThing> collection, String controllerId, int id) {
        lock.lock();
        try {
            return collection.get(getKey(controllerId, id));
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
                        mappedLocs.clear();
                        mappedSensors.clear();
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
            switch (t.getType()) {
                case "sensor" -> {
                    int nrPorts = IntUtils.toInt(t.getProperty("ports")).orElse(1);
                    LOG.info("Configuring sensor: {} with {} nr of ports", t.getThingId(), nrPorts);
                    var properties = t.getProperties();
                    for(int i=0; i<nrPorts; i++) {
                        var key = "port" + (i+1);
                        if(properties.containsKey(key)) {
                            var oAddress = IntUtils.toInt(properties.get(key));
                            oAddress.ifPresent(sp -> mappedSensors.put(getKey(t.getControllerId(), sp), t));
                            LOG.info("Mapping thing: {} to sensor map with loc address: {}", t, oAddress);
                        } else {
                            LOG.warn("Invalid Sensor configuration, no port: {} configured on thing: {}", key, t.getThingId());
                        }
                    }
                }
                case "locomotive" -> {
                    var oLocAddress = IntUtils.toInt(t.getProperty(Locomotive.LOC_ADDRESS));
                    LOG.info("Mapping thing: {} to locomotives map with loc address: {}", t, oLocAddress);
                    oLocAddress.ifPresent(la -> mappedLocs.put(getKey(t.getControllerId(), la), t));
                }
                case null -> {
                    LOG.error("Invalid thing presented, no type defined: {}", t);
                }
                default -> {
                    LOG.debug("We have an unmapped thing, or no type specifier: {}", t);
                }
            }
        }
    }

    private static String getKey(String controllerId, int id) {
        return controllerId + "-" + id;
    }
}
