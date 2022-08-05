package com.oberasoftware.iot.core.robotics.sensors;

import com.oberasoftware.iot.core.robotics.ActivatableCapability;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface SensorDriver<T extends Port> extends ActivatableCapability {

    List<T> getPorts();

    T getPort(String portId);
}
