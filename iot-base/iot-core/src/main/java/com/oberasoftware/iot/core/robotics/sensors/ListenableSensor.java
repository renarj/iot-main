package com.oberasoftware.iot.core.robotics.sensors;

/**
 * @author Renze de Vries
 */
public interface ListenableSensor<T extends SensorValue> extends SingeValueSensor {
    void listen(SensorListener<T> listener);
}
