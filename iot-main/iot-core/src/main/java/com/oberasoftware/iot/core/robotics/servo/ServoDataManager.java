package com.oberasoftware.iot.core.robotics.servo;

import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface ServoDataManager {
    Map<ServoProperty, Object> getCurrentValues(String servoId);

    <T> T readServoProperty(String servoId, ServoProperty property);

    boolean readServoProperties(String servoId, ServoProperty... properties);
}
