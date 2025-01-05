package com.oberasoftware.iot.core.robotics.commands;

import com.oberasoftware.iot.core.robotics.servo.ServoCommand;

/**
 * @author renarj
 */
public class ReadAngleLimit implements ServoCommand {

    private final String servoId;

    public ReadAngleLimit(String servoId) {
        this.servoId = servoId;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    @Override
    public String toString() {
        return "ReadAngleLimit{" +
                "servoId='" + servoId + '\'' +
                '}';
    }
}
