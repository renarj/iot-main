package com.oberasoftware.iot.core.robotics.commands;

import com.oberasoftware.iot.core.robotics.servo.ServoCommand;

public class RebootCommand implements ServoCommand {
    private final String servoId;

    public RebootCommand(String servoId) {
        this.servoId = servoId;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    @Override
    public String toString() {
        return "RebootCommand{" +
                "servoId='" + servoId + '\'' +
                '}';
    }
}
