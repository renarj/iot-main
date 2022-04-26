package com.oberasoftware.max.core.behaviours.gripper.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.ServoBehaviour;
import com.oberasoftware.robo.api.behavioural.gripper.GripperBehaviour;

/**
 * @author renarj
 */
public class GripperImpl implements GripperBehaviour {

    private final ServoBehaviour leftArm;
    private final ServoBehaviour rightArm;

    public GripperImpl(ServoBehaviour leftArm, ServoBehaviour rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robot) {
        leftArm.initialize(behaviouralRobot, robot);
        rightArm.initialize(behaviouralRobot, robot);
    }

    @Override
    public void open() {
        leftArm.goToMinimum(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
        rightArm.goToMinimum(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
    }

    @Override
    public void rest() {
        leftArm.goToDefault(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
        rightArm.goToDefault(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
    }

    @Override
    public void open(int percentage) {
        leftArm.goToPosition(10, ServoBehaviour.DEFAULT_SPEED_SCALE, percentage);
        rightArm.goToPosition(10, ServoBehaviour.DEFAULT_SPEED_SCALE, percentage);
    }

    @Override
    public void close() {
        leftArm.goToMaximum(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
        rightArm.goToMaximum(50, ServoBehaviour.DEFAULT_SPEED_SCALE);
    }

    @Override
    public void close(int percentage) {

    }
}
