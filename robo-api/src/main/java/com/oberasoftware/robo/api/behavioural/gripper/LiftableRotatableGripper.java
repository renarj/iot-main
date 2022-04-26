package com.oberasoftware.robo.api.behavioural.gripper;

/**
 * @author renarj
 */
public interface LiftableRotatableGripper extends RotatableGripper {
    void lift(int percentage);
}
