package com.oberasoftware.iot.core.robotics.commands;


import com.oberasoftware.iot.core.robotics.servo.ServoCommand;

/**
 * @author Renze de Vries
 */
public class TorgueCommand implements ServoCommand {
    private final String servoId;
    private final boolean enableTorque;

    public TorgueCommand(String servoId, boolean enableTorque) {
        this.servoId = servoId;
        this.enableTorque = enableTorque;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    public boolean isEnableTorque() {
        return enableTorque;
    }

    @Override
    public String toString() {
        return "TorgueCommand{" +
                "servoId=" + servoId +
                ", enableTorque=" + enableTorque +
                '}';
    }
}
