package com.oberasoftware.max.core.behaviours.gripper;

/**
 * @author renarj
 */
public interface LiftableRotatableGripper extends RotatableGripper {
    void lift(int percentage);
}
