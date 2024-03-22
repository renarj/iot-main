package com.oberasoftware.robo.maximus;

import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RobotInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(RobotInitializer.class);

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private AgentControllerInformation controllerInformation;

    public void initialize() {
        try {
            var robots = thingClient.getThings(controllerInformation.getControllerId(), RobotExtension.ROBOT_EXTENSION, "robot");
            robots.forEach(this::initRobot);
        } catch (IOTException e) {
            LOG.error("", e);
        }
    }

    private void initRobot(IotThing robot) {
        LOG.info("Initializing robot: {}", robot.getThingId());
        

    }
}
