package com.oberasoftware.robomax.core;

import com.google.common.collect.Lists;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.sensors.DirectPort;
import com.oberasoftware.robo.api.sensors.SensorDriver;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.api.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.core.commands.ReadPositionAndSpeedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

/**
 * @author Renze de Vries
 */
@Component
public class ServoSensorDriver implements SensorDriver<DirectPort<PositionValue>>, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ServoSensorDriver.class);

    @Autowired
    private ServoDriver servoDriver;

    @Autowired
    private LocalEventBus localEventBus;

    private Map<String, ServoPort> ports = new HashMap<>();

    private static final int SERVO_CHECK_INTERVAL = 100;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public List<DirectPort<PositionValue>> getPorts() {
        return Lists.newArrayList(ports.values());
    }

    @Override
    public DirectPort<PositionValue> getPort(String portId) {
        return ports.get(portId);
    }

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        LOG.info("Activating servo driver");
        servoDriver.getServos().forEach(s -> {
            LOG.info("Activating servo port: {}", s.getId());
            ports.put(s.getId(), new ServoPort());
        });

        executorService.submit(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                servoDriver.getServos().forEach(s -> {
                    localEventBus.publish(new ReadPositionAndSpeedCommand(s.getId()));
                    sleepUninterruptibly(SERVO_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
                });
            }
        });
    }

    @EventSubscribe
    public void receive(ServoUpdateEvent servoUpdate) {
        LOG.info("Received servo update: {}", servoUpdate);
        ports.get(servoUpdate.getServoId()).notify(servoUpdate);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
