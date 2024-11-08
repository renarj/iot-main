package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;

public interface Activator {
    String getSchemaId();

    List<IotThing> getDependents(RobotContext context, IotThing activatable);

    void activate(RobotContext context, IotThing activatable);
}
