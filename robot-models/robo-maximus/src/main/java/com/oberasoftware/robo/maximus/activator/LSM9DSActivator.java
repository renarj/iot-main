package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.sensors.LSM9DS1GyroSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LSM9DSActivator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(LSM9DSActivator.class);

    @Override
    public String getSchemaId() {
        return "LSM9DS1";
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        return List.of();
    }

    @Override
    public void activate(RobotContext context, IotThing activatable) {
        LOG.info("Activating LSDM9DS sensor for thing: {} on controller: {}", activatable.getThingId(), activatable.getControllerId());
        context.getRobotBuilder().sensor(new LSM9DS1GyroSensor(activatable.getControllerId(), activatable.getThingId()));
    }
}
