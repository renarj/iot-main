package com.oberasoftware.iot.core.robotics.motion;

/**
 * @author Renze de Vries
 */
public interface JointTarget {
    String getJointId();

    int getTargetPosition();

    int getTargetAngle();

    int getTargetVelocity();
}
