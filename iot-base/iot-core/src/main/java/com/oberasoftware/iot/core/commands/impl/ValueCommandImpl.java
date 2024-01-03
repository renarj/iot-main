package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class ValueCommandImpl implements ItemValueCommand {

    private final String thingId;
    private final String controllerId;

    private Map<String, Value> attributes = new HashMap<>();

    public ValueCommandImpl(String controllerId, String thingId, Map<String, Value> attributes) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attributes = attributes;
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
    public Value getAttribute(String property) {
        return attributes.get(property);
    }

    @Override
    public Map<String, Value> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "ValueCommandImpl{" +
                "thingId='" + thingId + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
