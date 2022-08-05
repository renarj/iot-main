package com.oberasoftware.iot.core.robotics.events;

/**
 * @author Renze de Vries
 */
public interface SensorEvent<T> extends RobotEvent {
    T getValue();
}
