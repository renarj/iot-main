package com.oberasoftware.iot.core.events.impl;

import com.oberasoftware.iot.core.events.ThingValueEvent;
import com.oberasoftware.iot.core.model.states.Value;

/**
 * @author renarj
 */
public class ThingValueEventImpl implements ThingValueEvent {
    private final String controllerId;
    private final String thingId;
    private final Value value;
    private final String attribute;

    public ThingValueEventImpl(String controllerId, String thingId, Value value, String attribute) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.value = value;
        this.attribute = attribute;
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
    public Value getValue() {
        return value;
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "DeviceValueEventImpl{" +
                "controllerId='" + controllerId + '\'' +
                ", deviceId='" + thingId + '\'' +
                ", value=" + value +
                ", label='" + attribute + '\'' +
                '}';
    }
}
