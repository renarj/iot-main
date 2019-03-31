package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;

import java.util.List;

public class MotionControlImpl implements MotionControl {
    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {

    }

    @Override
    public JointData getJoint(String jointId) {
        return null;
    }

    @Override
    public List<JointData> getJoints() {
        return null;
    }

    @Override
    public void setJointPosition(JointData position) {

    }

    @Override
    public void setJointPositions(List<JointData> jointPositions) {

    }
}
