package com.oberasoftware.iot.core.robotics;

import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface ActivatableCapability extends Capability {
    void activate(RobotHardware robot, Map<String, String> properties);

    void shutdown();
}
