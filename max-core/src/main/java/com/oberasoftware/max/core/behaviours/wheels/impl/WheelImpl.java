package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.max.core.behaviours.wheels.Wheel;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.ServoDriver;

/**
 * @author renarj
 */
public class WheelImpl implements Wheel {
    private ServoDriver servoDriver;

    private final String servoId;

    public WheelImpl(String servoId) {
        this.servoId = servoId;
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
        move(Math.abs(speed));
    }

    @Override
    public void backward(int speed) {
        //ensure its negative
        move(-Math.abs(speed));
    }

    @Override
    public void move(int speed) {
        Scale inputScale = new Scale(-100, 100);
        if(inputScale.isValid(speed)) {
            servoDriver.setServoSpeed(servoId, speed, inputScale);
        } else {
            throw new IllegalArgumentException("Invalid speed: " + speed + " needs to match scale: " + inputScale);
        }
    }

    @Override
    public void stop() {
        servoDriver.setServoSpeed(servoId, 0, new Scale(-100, 100));
    }
}
