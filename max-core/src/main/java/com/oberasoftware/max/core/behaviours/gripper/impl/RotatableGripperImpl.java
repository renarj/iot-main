package com.oberasoftware.max.core.behaviours.gripper.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.max.core.behaviours.gripper.RotatableGripper;
import com.oberasoftware.max.core.behaviours.servos.ServoBehaviour;

/**
 * @author renarj
 */
public class RotatableGripperImpl extends GripperImpl implements RotatableGripper {
    private final ServoBehaviour rotator;

    public RotatableGripperImpl(ServoBehaviour leftArm, ServoBehaviour rightArm, ServoBehaviour rotator) {
        super(leftArm, rightArm);
        this.rotator = rotator;
    }

    @Override
    public void initialize(Robot robot) {
        super.initialize(robot);
        rotator.initialize(robot);
    }

    @Override
    public void rotate(int degrees) {
        if(degrees < 0 || degrees > 360) {
            throw new IllegalArgumentException("Invalid degrees provided needs to be between 0-360");
        }

        int percentage = (int)((100 / (double)360) * degrees);
        rotator.goToPosition(percentage);
    }

    @Override
    public void rotateLevel() {
        rotator.goToDefault();
    }

    @Override
    public void rest() {
        rotator.goToDefault();
        super.rest();
    }
}
