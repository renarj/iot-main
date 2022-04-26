package com.oberasoftware.robo.api.sensors;

import com.oberasoftware.robo.api.model.Value;

/**
 * @author Renze de Vries
 */
public interface SensorValue<T> {
    T getRaw();
}
