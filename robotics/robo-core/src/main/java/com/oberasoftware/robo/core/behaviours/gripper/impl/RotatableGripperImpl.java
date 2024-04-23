package com.oberasoftware.robo.core.behaviours.gripper.impl;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.ServoBehaviour;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.RotatableGripper;

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
    public void initialize(Robot behaviouralRobot, RobotHardware robot) {
        super.initialize(behaviouralRobot, robot);
        rotator.initialize(behaviouralRobot, robot);
    }

    @Override
    public void rotate(int degrees) {
        if(degrees < 0 || degrees > 360) {
            throw new IllegalArgumentException("Invalid degrees provided needs to be between 0-360");
        }

        int percentage = (int)((100 / (double)360) * degrees);
        rotator.goToPosition(10, ServoBehaviour.DEFAULT_SPEED_SCALE, percentage);
    }

    @Override
    public void rotateLevel() {
        rotator.goToDefault(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
    }

    @Override
    public void rest() {
        rotator.goToDefault(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
        super.rest();
    }
}
