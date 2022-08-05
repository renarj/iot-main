package com.oberasoftware.iot.core.robotics.events;

import com.oberasoftware.base.event.Event;

import java.util.Map;
import java.util.Set;

public interface RobotValueEvent extends Event {
    String getSourcePath();

    Set<String> getAttributes();

    Map<String, ?> getValues();

    <T> T getValue(String attribute);
}
