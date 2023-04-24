package com.oberasoftware.trainautomation.rocoz21;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.trainautomation.rocoz21.commands.GetLanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Z21KeepAlive implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Z21KeepAlive.class);

    @Autowired
    private Z21Connector z21Connector;

    @Override
    public void run() {
        LOG.info("Starting background ping to Z21");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                z21Connector.send(new GetLanStatus());
                Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);
            } catch (IOTException e) {
                LOG.error("Could not get lan status", e);
            }
        }
        LOG.info("Background Ping thread has stopped");
    }
}
