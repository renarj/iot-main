package com.oberasoftware.iot.core.robotics.motion;

/**
 * @author Renze de Vries
 */
public interface JointTarget {
    String getServoId();

    int getTargetPosition();

    int getTargetAngle();

    int getTargetVelocity();
}
