package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.robotics.events.RobotValueEvent;

import java.util.Map;
import java.util.Set;

public class SensorDataImpl implements RobotValueEvent {
    private final Value value;
    private final String controllerId;
    private final String thingId;
    private final String attribute;

    public SensorDataImpl(String controllerId, String thingId, String attribute, Value value) {
        this.value = value;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attribute = attribute;
    }

    @Override
    public String getSourcePath() {
        return controllerId + "." + thingId + "." + attribute;
    }

    @Override
    public Set<String> getAttributes() {
        return Sets.newHashSet(attribute);
    }

    @Override
    public Map<String, ?> getValues() {
        return ImmutableBiMap.<String, Object>builder()
                .put(attribute, value.getValue())
                .build();
    }

    @Override
    public <T> T getValue(String attribute) {
        if(this.attribute.equalsIgnoreCase(attribute)) {
            return value.getValue();
        } else {
            return null;
        }
    }

    public Value getValue() {
        return value;
    }

    public String getControllerId() {
        return controllerId;
    }

    public String getThingId() {
        return thingId;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "SensorDataImpl{" +
                "value=" + value +
                ", controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
