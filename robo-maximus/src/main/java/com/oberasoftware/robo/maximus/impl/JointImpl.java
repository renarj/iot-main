package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoDriver;

public class JointImpl implements Joint {

    private final String name;
    private final String type;

    private Servo servo;
    private ServoDriver servoDriver;

    public JointImpl(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        this.servoDriver = robotCore.getServoDriver();
        this.servo = this.servoDriver.getServo(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getJointType() {
        return type;
    }

    @Override
    public boolean moveTo(int degrees) {
        return false;
    }
}
