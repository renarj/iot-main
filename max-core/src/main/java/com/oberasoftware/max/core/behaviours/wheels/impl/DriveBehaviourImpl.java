package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.DriveBehaviour;
import com.oberasoftware.robo.api.behavioural.DriveTrain;
import com.oberasoftware.robo.api.behavioural.Wheel;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.navigation.DirectionalInput;

import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author renarj
 */
public class DriveBehaviourImpl implements DriveBehaviour {

    private DriveTrain left;
    private DriveTrain right;

    public DriveBehaviourImpl(DriveTrain left, DriveTrain right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robot) {
        left.initialize(behaviouralRobot, robot);
        right.initialize(behaviouralRobot, robot);
    }

    @Override
    public void forward(int speed, Scale scale) {
        left.forward(speed);
        right.forward(speed);
    }

    @Override
    public void left(int speed, Scale scale) {
        left.forward(speed);
        right.backward(speed);
    }

    @Override
    public void backward(int speed, Scale scale) {
        left.backward(speed);
        right.backward(speed);
    }

    @Override
    public void drive(DirectionalInput input, Scale scale) {

    }

    @Override
    public void right(int speed, Scale scale) {
        left.backward(speed);
        right.forward(speed);
    }

    @Override
    public void stop() {
        left.stop();
        right.stop();
    }

    @Override
    public List<Wheel> getWheels() {
        return newArrayList(concat(left.getWheels(), right.getWheels()));
    }
}
