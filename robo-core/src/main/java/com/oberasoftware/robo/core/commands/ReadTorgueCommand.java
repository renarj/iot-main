package com.oberasoftware.robo.core.commands;

import com.oberasoftware.robo.api.servo.ServoCommand;

public class ReadTorgueCommand implements ServoCommand {
    private final String servoId;

    public ReadTorgueCommand(String servoId) {
        this.servoId = servoId;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    @Override
    public String toString() {
        return "ReadTorgueCommand{" +
                "servoId='" + servoId + '\'' +
                '}';
    }
}
