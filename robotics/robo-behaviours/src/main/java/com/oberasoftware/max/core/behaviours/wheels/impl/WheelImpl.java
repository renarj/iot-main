package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class WheelImpl implements Wheel {
    private static final Logger LOG = getLogger(WheelImpl.class);

    private ServoDriver servoDriver;

    private final String servoId;
    private final boolean reversed;

    public WheelImpl(String servoId, boolean reversed) {
        this.servoId = servoId;
        this.reversed = reversed;
    }

    @Override
    public String getName() {
        return servoId;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robot) {
        this.servoDriver = robot.getServoDriver();
    }

    @Override
    public void forward(int speed) {
        int actualSpeed = Math.abs(speed);
        if(reversed) {
            actualSpeed = -actualSpeed;
        }

        move(actualSpeed);
    }

    @Override
    public void backward(int speed) {
        int actualSpeed = -Math.abs(speed);
        if(reversed) {
            actualSpeed = -actualSpeed;
        }

        move(actualSpeed);
    }

    @Override
    public void move(int speed) {
        Scale inputScale = new Scale(-100, 100);
        if(inputScale.isValid(speed)) {
            LOG.info("Setting input speed to: {} for servo: {}", speed, servoId);
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
