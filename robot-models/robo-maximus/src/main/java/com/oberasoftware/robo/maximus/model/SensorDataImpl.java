package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.robotics.events.RobotValueEvent;
import com.oberasoftware.iot.core.robotics.sensors.SensorValue;

import java.util.Map;
import java.util.Set;

public class SensorDataImpl implements RobotValueEvent {
    private final SensorValue value;
    private final String sensorId;
    private final String attribute;

    public SensorDataImpl(SensorValue value, String sensorId, String attribute) {
        this.value = value;
        this.sensorId = sensorId;
        this.attribute = attribute;
    }

    @Override
    public String getSourcePath() {
        return sensorId + "." + attribute;
    }

    @Override
    public Set<String> getAttributes() {
        return Sets.newHashSet(attribute);
    }

    @Override
    public Map<String, ?> getValues() {
        return ImmutableBiMap.<String, Object>builder()
                .put(attribute, value.getRaw())
                .build();
    }

    @Override
    public <T> T getValue(String attribute) {
        if(this.attribute.equalsIgnoreCase(attribute)) {
            return (T) value.getRaw();
        } else {
            return null;
        }
    }

    public SensorValue getValue() {
        return value;
    }

    public String getSensorId() {
        return sensorId;
    }

    @Override
    public String toString() {
        return "SensorDataImpl{" +
                "value=" + value +
                ", sensorId='" + sensorId + '\'' +
                '}';
    }
}
