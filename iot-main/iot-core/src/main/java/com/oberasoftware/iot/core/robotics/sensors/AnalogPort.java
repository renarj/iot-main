package com.oberasoftware.iot.core.robotics.sensors;

/**
 * @author Renze de Vries
 */
public interface AnalogPort extends Port {
    void listen(PortListener<VoltageValue> portListener);
}
