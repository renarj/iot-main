package com.oberasoftware.iot.core.robotics.behavioural.gripper;

/**
 * @author renarj
 */
public interface RotatableGripper extends GripperBehaviour {
    void rotate(int degrees);

    void rotateLevel();
}
