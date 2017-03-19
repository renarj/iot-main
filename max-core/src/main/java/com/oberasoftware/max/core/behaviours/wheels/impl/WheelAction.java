package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.robo.api.servo.ServoDriver;

/**
 * @author renarj
 */
@FunctionalInterface
public interface WheelAction {
    void doAction(String servoId, int speed, ServoDriver servoDriver);
}
