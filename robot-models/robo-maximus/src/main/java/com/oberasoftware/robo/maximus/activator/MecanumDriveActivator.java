package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.drive.MecanumDriveTrainImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MecanumDriveActivator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(MecanumDriveActivator.class);

    @Override
    public String getSchemaId() {
        return "meccanum";
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        return List.of();
    }

    @Override
    public void activate(RobotContext context, IotThing activatable) {
        var frontRight = safeGetProperty(activatable, "frontRight");
        var frontLeft = safeGetProperty(activatable,"frontLeft");
        var rearRight = safeGetProperty(activatable,"rearRight");
        var rearLeft = safeGetProperty(activatable,"rearLeft");

        var wheelController = new MecanumDriveTrainImpl(frontLeft, frontRight, rearLeft, rearRight);
        LOG.info("Configured wheel controller: {}", wheelController);
        context.getRobotBuilder().behaviourController(wheelController);
    }

    private String safeGetProperty(IotThing thing, String property) {
        var props = thing.getProperties();
        if(props.containsKey(property)) {
            return props.get(property);
        } else {
            throw new RuntimeIOTException("Could not find property: " + property);
        }
    }
}
