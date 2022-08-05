package com.oberasoftware.iot.core.robotics.sensors;

/**
 * @author Renze de Vries
 */
public interface PortListener<T extends SensorValue> {
    void receive(T e);
}
