package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.max.core.behaviours.wheels.Wheel;

/**
 * @author renarj
 */
public class WheelImpl implements Wheel {
    private ServoDriver servoDriver;

    private final String servoId;
    private final WheelAction forwardAction;
    private final WheelAction backwardAction;

    private boolean reversed = false;

    public WheelImpl(String servoId, WheelAction forwardAction, WheelAction backwardAction) {
        this.servoId = servoId;
        this.forwardAction = forwardAction;
        this.backwardAction = backwardAction;
    }

    public WheelImpl(String servoId, boolean reversed, WheelAction forwardAction, WheelAction backwardAction) {
        this.servoId = servoId;
        this.reversed = reversed;
        this.forwardAction = forwardAction;
        this.backwardAction = backwardAction;
    }

    @Override
    public String getName() {
        return servoId;
    }

    @Override
    public void initialize(Robot robot) {
        this.servoDriver = robot.getServoDriver();
    }

    @Override
    public void forward(int speed) {
        if(reversed) {
            backwardAction.doAction(servoId, speed, servoDriver);
        } else {
            forwardAction.doAction(servoId, speed, servoDriver);
        }
    }

    @Override
    public void backward(int speed) {
        if(reversed) {
            forwardAction.doAction(servoId, speed, servoDriver);
        } else {
            backwardAction.doAction(servoId, speed, servoDriver);
        }
    }

    @Override
    public void stop() {
        servoDriver.setServoSpeed(servoId, 0);
    }
}
