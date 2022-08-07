package com.oberasoftware.iot.core.robotics.behavioural.gripper;

/**
 * @author renarj
 */
public interface LiftableRotatableGripper extends RotatableGripper {
    void lift(int percentage);
}
