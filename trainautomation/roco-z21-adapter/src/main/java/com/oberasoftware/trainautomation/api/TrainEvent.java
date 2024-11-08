package com.oberasoftware.trainautomation.api;

import com.oberasoftware.iot.core.events.ValueEvent;
import com.oberasoftware.iot.core.model.states.Value;

public abstract class TrainEvent implements ValueEvent {
    private final int eventAddress;
    private final String attribute;
    private final Value value;

    public TrainEvent(int eventAddress, String attribute, Value value) {
        this.eventAddress = eventAddress;
        this.attribute = attribute;
        this.value = value;
    }

    public int getEventAddress() {
        return eventAddress;
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LocEvent{" +
                "locAddress=" + eventAddress +
                ", attribute='" + attribute + '\'' +
                ", value=" + value +
                '}';
    }
}
