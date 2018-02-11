package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.max.core.behaviours.wheels.DriveBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.DriveTrain;
import com.oberasoftware.max.core.behaviours.wheels.Wheel;
import com.oberasoftware.robo.api.Robot;

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
    public void initialize(Robot robot) {
        left.initialize(robot);
        right.initialize(robot);
    }

    @Override
    public void forward(int speed) {
        left.forward(speed);
        right.forward(speed);
    }

    @Override
    public void left(int speed) {
        left.forward(speed);
        right.backward(speed);
    }

    @Override
    public void backward(int speed) {
        left.backward(speed);
        right.backward(speed);
    }

    @Override
    public void drive(int speed, DIRECTION direction) {
        switch(direction) {
            case LEFT:
                left(speed);
                break;
            case RIGHT:
                right(speed);
                break;
            case FORWARD:
                forward(speed);
                break;
            case BACKWARD:
                backward(speed);
                break;
        }
    }

    @Override
    public void right(int speed) {
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
