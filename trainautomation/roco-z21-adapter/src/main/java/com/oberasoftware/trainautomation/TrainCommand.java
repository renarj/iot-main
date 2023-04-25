package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.train.StepMode;

import java.util.Map;

public class TrainCommand extends ValueCommandImpl {

    private final int locAddress;
    private final StepMode stepMode;

    public TrainCommand(String controllerId, String thingId, Map<String, Value> values, int locAddress, StepMode stepMode) {
        super(controllerId, thingId, values);
        this.locAddress = locAddress;
        this.stepMode = stepMode;
    }

    public int getLocAddress() {
        return locAddress;
    }

    public StepMode getStepMode() {
        return stepMode;
    }

    @Override
    public String toString() {
        return "TrainCommand{" +
                "locAddress=" + locAddress +
                ", stepMode=" + stepMode +
                '}';
    }
}
