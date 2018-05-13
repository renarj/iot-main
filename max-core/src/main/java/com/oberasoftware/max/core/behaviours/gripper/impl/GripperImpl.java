package com.oberasoftware.max.core.behaviours.gripper.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.GripperBehaviour;
import com.oberasoftware.robo.api.behavioural.ServoBehaviour;

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
        leftArm.goToMinimum();
        rightArm.goToMinimum();
    }

    @Override
    public void rest() {
        leftArm.goToDefault();
        rightArm.goToDefault();
    }

    @Override
    public void open(int percentage) {
        leftArm.goToPosition(percentage);
        rightArm.goToPosition(percentage);
    }

    @Override
    public void close() {
        leftArm.goToMaximum();
        rightArm.goToMaximum();
    }

    @Override
    public void close(int percentage) {

    }
}
