package com.oberasoftware.iot.integrations.shelly;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.events.impl.ThingMultiValueEventImpl;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.states.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ShellyStatusSync implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ShellyStatusSync.class);

    private static final int DEFAULT_INTERVAL = 20000;

    @Autowired
    private ShellyConnector connector;

    @Autowired
    private LocalEventBus eventBus;

    private final Map<String, ShellyMetadata> syncList = new ConcurrentHashMap<>();

    public void addShelly(ShellyMetadata shellyMetadata) {
        LOG.info("Tracking shelly: {}", shellyMetadata.getIp());
        this.syncList.put(shellyMetadata.getIp(), shellyMetadata);
    }

    public boolean isTracking(String shellyIp) {
        return syncList.containsKey(shellyIp);
    }

    @Override
    public void run() {
        LOG.info("Starting Shelly sync thread");
        while(!Thread.interrupted()) {
            try {
                syncStatus();
            } catch(Exception e){
                LOG.error("Unknown error happened syncing Shelly status", e);
            }

            Uninterruptibles.sleepUninterruptibly(DEFAULT_INTERVAL, TimeUnit.MILLISECONDS);
        }

        LOG.info("Shelly sync thread stopped");
    }

    private void syncStatus() {
        LOG.debug("Starting sync of shellys: {}", syncList);
        syncList.forEach((k, sh) -> {
            try {
                Map<String, Value> values = connector.getValues(sh.getIp(), sh.getShellyComponents());
                LOG.info("Found Shelly values: {}", values);

                eventBus.publish(new ThingMultiValueEventImpl(sh.getControllerId(), sh.getThingId(), values));
            } catch (IOTException e) {
                LOG.error("Could not retrieve shelly: " + sh.getIp() + " status", e);
            }
        });
    }
}
