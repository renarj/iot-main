package com.oberasoftware.robo.api.sensors;

/**
 * @author Renze de Vries
 */
public interface PortListener<T extends SensorValue> {
    void receive(T e);
}
