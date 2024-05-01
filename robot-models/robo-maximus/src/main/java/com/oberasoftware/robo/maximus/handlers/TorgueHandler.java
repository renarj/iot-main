package com.oberasoftware.robo.maximus.handlers;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TorgueHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TorgueHandler.class);

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private ThingClient thingClient;

    @Override
    public Set<String> getSupportedAttributes() {
        return Sets.newHashSet("torgue");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.debug("Executing Torgue command: {}", command);
        RobotHardware robot = robotRegistry.getRobot(command.getThingId());
        ServoDriver servoDriver = robot.getServoDriver();

        try {
            var oThing = thingClient.getThing(command.getControllerId(), command.getThingId());
            if(oThing.isPresent()) {
                var thing = oThing.get();
                var servoId = thing.getProperty("servo_id");

                Value value = command.getAttribute(attribute);
                boolean torgueEnabled = value.asString().equalsIgnoreCase("ON") || value.asString().equalsIgnoreCase("TRUE");

                if(servoId != null) {
                    LOG.info("Setting torgue for servo: {} to: {}", servoId, torgueEnabled);
                    servoDriver.setTorgue(servoId, torgueEnabled);
                } else {
                    LOG.info("Setting torgue for all servos to: {}", torgueEnabled);
                    servoDriver.setTorgueAll(torgueEnabled);
                }
            } else {
                LOG.warn("Got command: {} but could not find thing: {}", command, command.getThingId());
            }
        } catch (IOTException e) {
            throw new RuntimeIOTException("Could not retrieve remote Thing details", e);
        }
    }
}
