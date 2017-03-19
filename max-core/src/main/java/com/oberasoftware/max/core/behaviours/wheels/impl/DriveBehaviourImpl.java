package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.max.core.behaviours.wheels.DriveBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.DriveTrain;

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
    public void right(int speed) {
        left.backward(speed);
        right.forward(speed);
    }

    @Override
    public void stop() {
        left.stop();
        right.stop();
    }
}
