package com.oberasoftware.robo.maximus.model;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.robo.api.sensors.SensorValue;

public class SensorDataImpl implements Event {
    private final SensorValue value;
    private final String sensorId;

    public SensorDataImpl(SensorValue value, String sensorId) {
        this.value = value;
        this.sensorId = sensorId;
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
