package com.oberasoftware.iot.core.robotics.sensors;


import com.oberasoftware.iot.core.robotics.events.SensorEvent;

/**
 * @author Renze de Vries
 */
public interface SensorListener<T extends SensorValue> {
    void receive(SensorEvent<T> event);
}
