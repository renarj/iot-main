package com.oberasoftware.robo.maximus.handlers;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.robotics.behavioural.ConfiguredRobotRegistery;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveBehaviour;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.navigation.DirectionalInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConditionalOnBean(AgentClient.class)
public class DriveHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DriveHandler.class);

    @Autowired
    private ConfiguredRobotRegistery configuredRobotRegistery;

    @Autowired
    private AgentClient agentClient;

    @Override
    public Set<String> getSupportedAttributes() {
        return Set.of("drive");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        LOG.info("Setting robot drive direction");

        try {
            var oThing = agentClient.getThing(command.getControllerId(), command.getThingId());
            if(oThing.isPresent()) {
                var thing = oThing.get();
                var robotId = thing.getParentId();

                handleRobot(robotId, command);
            } else {
                throw new RuntimeIOTException("Could not find Drive capability for robot");
            }
        } catch (IOTException e) {
            throw new RuntimeIOTException("Could not find Drive capability for robot", e);
        }
    }

    private void handleRobot(String robotId, ThingValueCommand command) {
        var oRobot  = configuredRobotRegistery.getRobot(robotId);
        if(oRobot.isPresent()) {
            var robot = oRobot.get();
            var db = robot.getBehaviour(DriveBehaviour.class);
            var di = getInput(command);

            LOG.info("Robot: {} found for drive command: {}", robot, di);
            db.drive(di, new Scale(-1, 1));
        }
    }

    private DirectionalInput getInput(ThingValueCommand command) {
        double x = command.getAttribute("x").getValue();
        double y = command.getAttribute("y").getValue();
        double z = command.getAttribute("z").getValue();

        return new DirectionalInput(ImmutableMap.<String, Double>builder()
                .put("x", x).put("y", y).put("z", z)
                .build());
    }
}
