package com.oberasoftware.trainautomation.api;

import com.oberasoftware.iot.core.model.states.Value;

public class SensorEvent extends TrainEvent{
    public SensorEvent(int eventAddress, String attribute, Value value) {
        super(eventAddress, attribute, value);
    }
}
