package com.oberasoftware.iot.core.robotics.commands;

import com.oberasoftware.iot.core.robotics.servo.ServoCommand;

/**
 * @author renarj
 */
public class AngleLimitCommand implements ServoCommand {
    private final String servoId;
    private final int minLimit;
    private final int maxLimit;

    public AngleLimitCommand(String servoId, int minLimit, int maxLimit) {
        this.servoId = servoId;
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    public int getMinLimit() {
        return minLimit;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    @Override
    public String toString() {
        return "AngleLimitCommand{" +
                "servoId='" + servoId + '\'' +
                ", minLimit=" + minLimit +
                ", maxLimit=" + maxLimit +
                '}';
    }
}
