package com.oberasoftware.iot.core.robotics.sensors;

public interface SingeValueSensor<T extends SensorValue> extends Sensor {
    String getName();

    T getValue();
}
