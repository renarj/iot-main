package com.oberasoftware.robo.maximus.sensors;

import com.oberasoftware.robo.api.sensors.SensorValue;

public class DoubleValue implements SensorValue<Double> {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public Double getRaw() {
        return value;
    }

    @Override
    public String toString() {
        return "DoubleValue{" +
                "value=" + value +
                '}';
    }
}
