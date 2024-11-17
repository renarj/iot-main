package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.sensors.Ina260CurrentSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Ina260Activator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(Ina260Activator.class);

    @Override
    public String getSchemaId() {
        return "Ina260";
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        return List.of();
    }

    @Override
    public void activate(RobotContext context, IotThing activatable) {
        LOG.info("Activating INA260 sensor for thing: {} on controller: {}", activatable.getThingId(), activatable.getControllerId());
        context.getRobotBuilder().sensor(new Ina260CurrentSensor(activatable.getControllerId(), activatable.getThingId()));
    }
}