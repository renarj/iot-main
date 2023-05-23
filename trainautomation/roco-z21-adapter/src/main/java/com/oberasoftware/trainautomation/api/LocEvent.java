package com.oberasoftware.trainautomation.api;

import com.oberasoftware.iot.core.events.ValueEvent;
import com.oberasoftware.iot.core.model.states.Value;

public class LocEvent implements ValueEvent {
    private int locAddress;
    private String attribute;
    private Value value;

    public LocEvent(int locAddress, String attribute, Value value) {
        this.locAddress = locAddress;
        this.attribute = attribute;
        this.value = value;
    }

    public int getLocAddress() {
        return locAddress;
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public Value getValue() {
        return value;
    }
}
