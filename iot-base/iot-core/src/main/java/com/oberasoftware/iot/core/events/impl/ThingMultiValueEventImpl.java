package com.oberasoftware.iot.core.events.impl;

import com.oberasoftware.iot.core.events.ThingMultiValueEvent;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.Map;

public class ThingMultiValueEventImpl implements ThingMultiValueEvent {
    private final String controllerId;
    private final String thingId;

    private final Map<String, Value> values;

    public ThingMultiValueEventImpl(String controllerId, String thingId, Map<String, Value> values) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.values = values;
    }

    @Override
    public Map<String, Value> getValues() {
        return values;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    @Override
    public String toString() {
        return "ThingMultiValueEventImpl{" +
                "controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", values=" + values +
                '}';
    }
}
