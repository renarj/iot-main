package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.ServoRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WheelActivator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(WheelActivator.class);

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public String getSchemaId() {
        return "Wheel";
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        return List.of();
    }

    @Override
    public void activate(RobotContext context, IotThing activatable) {
        LOG.info("Trying to activate wheel: {}", activatable);

        String servoThingId = activatable.getProperty("servo");
        String servoId = servoRegistry.getThing(activatable.getControllerId(), servoThingId).getServoId();
        String reverseDirection = activatable.getProperty("reverseDirection");
        boolean directionReverse = reverseDirection != null && reverseDirection.equalsIgnoreCase("true");

        LOG.info("Found wheel: {} based on servo: {}", activatable, servoId);
        context.getRobotBuilder().wheel(activatable.getControllerId(), activatable.getThingId(), servoId, directionReverse);
    }
}
