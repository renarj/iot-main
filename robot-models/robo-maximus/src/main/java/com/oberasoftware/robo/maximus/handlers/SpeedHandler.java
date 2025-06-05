package com.oberasoftware.robo.maximus.handlers;

import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.robotics.behavioural.ConfiguredRobotRegistery;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.humanoid.ConfigurableRobot;
import com.oberasoftware.robo.maximus.ServoRegistry;
import com.oberasoftware.robo.maximus.ThingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class SpeedHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SpeedHandler.class);

    @Autowired
    private ConfiguredRobotRegistery robotRegistry;

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public Set<String> getSupportedAttributes() {
        return Set.of("speed");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.info("Setting speed of servo");

        if(command.getAttributes().containsKey(attribute)) {
            Long targetSpeed = command.getAttribute("speed").getValue();

            if(isWheel(command.getControllerId(), command.getThingId())) {
                LOG.info("Target of speed command is a wheel: {}", command.getThingId());
                var wheel = findWheel(command.getControllerId(), command.getThingId());

                var oRobot = robotRegistry.findRobotForWheel(command.getControllerId(), command.getThingId());
                setSpeed(oRobot, wheel.getServoId(), targetSpeed.intValue(), wheel.isReversed());
            } else {
                LOG.info("Target of speed command is a servo: {}", command.getThingId());
                ThingKey thingIdentifier = servoRegistry.getThing(command.getControllerId(), command.getThingId());
                if(thingIdentifier != null) {
                    var robotId = thingIdentifier.getRobotId();
                    var servoId = thingIdentifier.getServoId();

                    var oRobot = robotRegistry.getRobot(robotId);
                    setSpeed(oRobot, servoId, targetSpeed.intValue(), false);
                } else {
                    LOG.warn("Could not find servo with id {}", command.getThingId());
                }
            }
        }
    }

    private void setSpeed(Optional<ConfigurableRobot> oRobot, String servoId, int targetSpeed, boolean reverse) {
        if(oRobot.isPresent()) {
            var servoDriver = oRobot.get().getRobotCore().getServoDriver();

            if(reverse) {
                targetSpeed = -targetSpeed;
            }
            servoDriver.setServoSpeed(servoId, targetSpeed, new Scale(-100, 100));
        } else {
            LOG.warn("Could not find robot for servo with id {}", servoId);
        }
    }

    private boolean isWheel(String controllerId, String thingId) {
        return robotRegistry.findRobotForWheel(controllerId, thingId).isPresent();
    }

    private Wheel findWheel(String controllerId, String thingId) {
        var robots = robotRegistry.findRobotForWheel(controllerId, thingId);

        for (Wheel w : robots.get().getWheels()) {
            if(w.getThingId().equalsIgnoreCase(thingId)) {
                return w;
            }
        }

        LOG.info("Couldl not find wheel for id: {}", thingId);
        return null;
    }
}
