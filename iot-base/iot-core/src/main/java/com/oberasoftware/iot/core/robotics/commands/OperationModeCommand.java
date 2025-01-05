package com.oberasoftware.iot.core.robotics.commands;

import com.oberasoftware.iot.core.robotics.servo.ServoCommand;
import com.oberasoftware.iot.core.robotics.servo.StateManager;

public class OperationModeCommand implements ServoCommand {
    private final String servoId;
    private final StateManager.ServoMode operationMode;

    public OperationModeCommand(String servoId, StateManager.ServoMode operationMode) {
        this.servoId = servoId;
        this.operationMode = operationMode;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    public StateManager.ServoMode getOperationMode() {
        return operationMode;
    }

    @Override
    public String toString() {
        return "OperationModeCommand{" +
                "servoId='" + servoId + '\'' +
                ", operationMode=" + operationMode +
                '}';
    }
}
