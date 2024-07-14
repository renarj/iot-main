package com.oberasoftware.robo.maximus.handlers;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.iot.core.util.IntUtils;
import com.oberasoftware.robo.maximus.ServoRegistry;
import com.oberasoftware.robo.maximus.ThingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class PositionHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PositionHandler.class);
    private static final int DEFAULT_SPEED = 20;

    private static final Scale REMOTE_SCALE_SPEED = new Scale(-100, 100);
    private static final Scale REMOTE_SCALE_POSITION = new Scale(0, 4095);

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public Set<String> getSupportedAttributes() {
        return Sets.newHashSet("position");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.info("Executing Servo position command: {}", command);
        ThingKey thingIdentifier = servoRegistry.getThing(command.getControllerId(), command.getThingId());
        var robotId = thingIdentifier.getRobotId();
        var servoId = thingIdentifier.getServoId();
        RobotHardware robot = robotRegistry.getRobot(robotId);
        ServoDriver servoDriver = robot.getServoDriver();

        Long servoPosition = command.getAttribute("position").getValue();
        Optional<Value> speedOpt = Optional.ofNullable(command.getAttribute("speed"));

        if(speedOpt.isPresent()) {
            setServoPositionWithSpeed(servoDriver, servoId, servoPosition, speedOpt.get().getValue());
        } else {
            setServoPosition(servoDriver, servoId, servoPosition);
        }
    }

    private void setServoPositionWithSpeed(ServoDriver servoDriver, String servoId, Long servoPosition, String speed) {
        LOG.info("Setting servo: {} to position: {} with velocity: {}", servoId, servoPosition, speed);
        servoDriver.setPositionAndSpeed(servoId, IntUtils.toInt(speed, DEFAULT_SPEED), REMOTE_SCALE_SPEED,
                servoPosition.intValue(), REMOTE_SCALE_POSITION);
    }

    private void setServoPosition(ServoDriver servoDriver, String servoId, Long servoPosition) {
        LOG.info("Setting servo: {} to position: {}", servoId, servoPosition);
        servoDriver.setTargetPosition(servoId, servoPosition.intValue(), REMOTE_SCALE_POSITION);
    }
}