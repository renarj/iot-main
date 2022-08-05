package com.oberasoftware.iot.core.robotics.sensors;

import com.oberasoftware.iot.core.robotics.Robot;

import java.util.Map;
import java.util.Set;

public interface MultiValueSensor<T extends SensorValue> extends Sensor {

    T getValue(String attribute);

    Set<String> getAttributes();

    Map<String, T> getValues();

    default void activate(Robot robot, SensorDriver sensorDriver) {
        //no activiation needed by default
        activate(robot);
    }

    void activate(Robot robot);
}
