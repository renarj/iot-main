package com.oberasoftware.iot.core.robotics.events;

import com.oberasoftware.iot.core.robotics.sensors.TriggerValue;

public class BumperEvent implements SensorEvent<TriggerValue> {
    private final String source;
    private final String robotId;
    private final String capability;
    private final TriggerValue value;

    public BumperEvent(String robotId, String capability, String source, TriggerValue value) {
        this.source = source;
        this.robotId = robotId;
        this.capability = capability;
        this.value = value;
    }

    @Override
    public String getAttribute() {
        return source;
    }

    @Override
    public String getControllerId() {
        return robotId;
    }

    @Override
    public String getItemId() {
        return capability;
    }

    public boolean isTriggered() {
        return value.getRaw();
    }

    @Override
    public TriggerValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BumperEvent{" +
                "source='" + source + '\'' +
                ", robotId='" + robotId + '\'' +
                ", capability='" + capability + '\'' +
                ", label='" + getAttribute() + '\'' +
                ", controllerId='" + getControllerId() + '\'' +
                ", itemId='" + getItemId() + '\'' +
                ", value=" + getValue() +
                '}';
    }
}
