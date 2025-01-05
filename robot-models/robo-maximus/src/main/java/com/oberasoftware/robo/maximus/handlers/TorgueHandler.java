package com.oberasoftware.robo.maximus.handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.ConfiguredRobotRegistery;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.robo.maximus.ServoRegistry;
import com.oberasoftware.robo.maximus.ThingKey;
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
    private ConfiguredRobotRegistery configuredRobotRegistery;

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public Set<String> getSupportedAttributes() {
        return Sets.newHashSet("torgue");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.info("Executing Torgue command: {}", command);

        if(command.getAttributes().containsKey(attribute)) {
            Value value = command.getAttribute(attribute);
            boolean targetState = value.asString().equalsIgnoreCase("ON") || value.asString().equalsIgnoreCase("TRUE");

            if(robotRegistry.containsRobot(command.getThingId())) {
                LOG.info("Target of torgue command is entire robot: {}", command.getThingId());
                targetRobotTorgue(command, targetState);
            } else if(testIsJoint(command.getThingId())) {
                LOG.info("Target of torque command is a joint: {}", command.getThingId());
                targetJointTorgue(command.getThingId(), targetState);
            } else {
                LOG.info("Target of torque command is a servo: {}", command.getThingId());
                ThingKey thingIdentifier = servoRegistry.getThing(command.getControllerId(), command.getThingId());
                if(thingIdentifier != null) {
                    var robotId = thingIdentifier.getRobotId();
                    var servoId = thingIdentifier.getServoId();
                    targetServoTorgue(robotId, servoId, targetState);
                }
            }
        }
    }

    private Joint findJoint(String thingId) {
        var robots = configuredRobotRegistery.getRobots();

        for (Robot r : robots) {
            LOG.info("Checking robot: {}", r);
            var jc = r.getBehaviour(JointControl.class);
            var joint = jc.getJoint(thingId);
            LOG.info("Found joint: {} for thing: {}", joint, thingId);
            if(joint != null) {
                return joint;
            }
        }

        LOG.info("Couldl not find joint for id: {}", thingId);
        return null;
    }

    private boolean testIsJoint(String jointId) {
        return findJoint(jointId) != null;
    }

    private void targetRobotTorgue(ThingValueCommand command, boolean targetState) {
        if(command.getAttributes().containsKey("servos")) {
            var servos = command.getAttribute("servos").asString().split(",");
            Lists.newArrayList(servos).forEach(s -> {
                var sKey = servoRegistry.getThing(command.getControllerId(), s);
                if(sKey != null) {
                    targetServoTorgue(command.getThingId(), s, targetState);
                } else {
                    LOG.warn("Torgue command given for servo: {} but not known on robot: {}", s, command.getThingId());
                }
            });
        } else {
            LOG.debug("Robot torgue command given without servos, applying torgue: {} to robot: {}", targetState, command.getThingId());
            targetServoTorgue(command.getThingId(), null, targetState);
        }
    }

    private void targetJointTorgue(String thingId, boolean targetState) {
        var j = findJoint(thingId);
        if(j != null) {
            targetServoTorgue(j.getRobotId(), j.getServoId(), targetState);
        }
    }

    private void targetServoTorgue(String robotId, String servoId, boolean targetState) {
        RobotHardware robot = robotRegistry.getRobot(robotId);
        ServoDriver servoDriver = robot.getServoDriver();
        if(servoId != null) {
            LOG.info("Setting torgue for servo: {} to: {}", servoId, targetState);
            servoDriver.setTorgue(servoId, targetState);
        } else {
            LOG.info("Setting torgue for all servos to: {}", targetState);
            servoDriver.setTorgueAll(targetState);
        }
    }
}
