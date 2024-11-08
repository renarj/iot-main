package com.oberasoftware.robo.maximus.activator;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.DynamixelTorgueManager;
import com.oberasoftware.robo.maximus.ServoRegistry;
import com.oberasoftware.robo.maximus.sensors.ESP32SensorDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DynamixelActivator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelActivator.class);

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public String getSchemaId() {
        return "DynamixelServoDriver";
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        return new ArrayList<>();
    }

    @Override
    public void activate(RobotContext context, IotThing driver) {
        LOG.info("Configuring dynamixel servo driver: {}", driver.getThingId());
        try {
            String port = driver.getProperty("DXL_PORT");
            List<IotThing> servos = thingClient.getChildren(driver.getControllerId(), driver.getThingId(), "servo");
            if (servos.isEmpty()) {
                LOG.info("Servo driver configured, but no servo's specified, trigger Servo Scan");
                context.getHardwareBuilder()
                        .servoDriver(DynamixelServoDriver.class,
                                ImmutableMap.<String, String>builder()
                                        .put(DynamixelServoDriver.PORT, port)
                                        .build())
                        .capability(ServoSensorDriver.class)
                        .capability(DynamixelTorgueManager.class);
            } else {
                servos.forEach(s -> servoRegistry.addServo(s.getControllerId(), s.getThingId(), s.getProperty("servo_id"), context.getHardwareBuilder().getName()));
                String motorString = servos.stream().map(s -> s.getProperty("servo_id")).collect(Collectors.joining(","));
                LOG.info("Registering Dynamixel servo driver for servos: {} on port: {}", motorString, port);

                context.getHardwareBuilder()
                        .servoDriver(DynamixelServoDriver.class,
                                ImmutableMap.<String, String>builder()
                                        .put(DynamixelServoDriver.PORT, port)
                                        .put("motors", motorString)
                                        .build())
                        .capability(ServoSensorDriver.class)
                        .capability(DynamixelTorgueManager.class);
            }

            if (driver.hasProperty("sensors") && "true".equalsIgnoreCase(driver.getProperty("sensors"))) {
                LOG.info("Configuring ESP32 sensors");
                context.getHardwareBuilder().capability(ESP32SensorDriver.class);
            }
        } catch (IOTException e) {
            throw new RuntimeException(e);
        }
    }
}
