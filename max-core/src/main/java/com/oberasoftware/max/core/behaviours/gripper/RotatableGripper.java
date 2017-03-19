package com.oberasoftware.max.core.behaviours.gripper;

/**
 * @author renarj
 */
public interface RotatableGripper extends GripperBehaviour {
    void rotate(int degrees);

    void rotateLevel();
}
