package com.oberasoftware.trainautomation.api;

import com.oberasoftware.iot.core.model.states.Value;

public class LocEvent extends TrainEvent {
    public LocEvent(int eventAddress, String attribute, Value value) {
        super(eventAddress, attribute, value);
    }
}
