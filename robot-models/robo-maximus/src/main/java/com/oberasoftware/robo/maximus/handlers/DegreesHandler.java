package com.oberasoftware.robo.maximus.handlers;

import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.JointBasedRobotRegistery;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DegreesHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DegreesHandler.class);

    private static final Scale REMOTE_SCALE_POSITION = new Scale(0, 4095);

    @Autowired
    private JointBasedRobotRegistery robotRegistery;

    @Autowired
    private RobotRegistry hardwareRobotRegistry;

    @Override
    public Set<String> getSupportedAttributes() {
        return Set.of("degrees");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.info("Executing Joint position command: {}", command);
        var oRobot = robotRegistery.findRobotForJoint(command.getControllerId(), command.getThingId());
        if(oRobot.isPresent()) {
            RobotHardware robotHardware = hardwareRobotRegistry.getRobot(oRobot.get().getName());

            var oJoint = oRobot.get().getJoint(command.getThingId());
            if(oJoint.isPresent()) {
                Long degrees = command.getAttribute("degrees").getValue();
                int targetPosition = Scale.DEGREES_SCALE.convertToScale(degrees.intValue(), REMOTE_SCALE_POSITION);

                LOG.info("Setting joint: {} to degrees/position: {}/{}", command.getThingId(), degrees, targetPosition);
                robotHardware.getServoDriver().setTargetPosition(oJoint.get().getServoId(), targetPosition, REMOTE_SCALE_POSITION);
            } else {
                LOG.warn("Got a joint position command, but could not find the joint: {}", command.getThingId());
            }
        } else {
            LOG.warn("Got a joint position command, but could not find the associated robot for: {}", command.getThingId());
        }
    }
}
