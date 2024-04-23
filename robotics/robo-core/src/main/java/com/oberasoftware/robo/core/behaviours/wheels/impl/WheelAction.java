package com.oberasoftware.robo.core.behaviours.wheels.impl;

import com.oberasoftware.iot.core.robotics.servo.ServoDriver;

/**
 * @author renarj
 */
@FunctionalInterface
public interface WheelAction {
    void doAction(String servoId, int speed, ServoDriver servoDriver);
}
