package com.oberasoftware.iot.core.robotics.events;

import com.oberasoftware.iot.core.robotics.sensors.DistanceValue;

/**
 * @author Renze de Vries
 */
public class DistanceSensorEvent implements SensorEvent<DistanceValue> {
    private final String source;
    private final String robotId;
    private final String capability;
    private final int distance;

    public DistanceSensorEvent(String robotId, String capability, String source, int distance) {
        this.source = source;
        this.robotId = robotId;
        this.capability = capability;
        this.distance = distance;
    }

    @Override
    public String getControllerId() {
        return robotId;
    }

    @Override
    public String getItemId() {
        return capability;
    }

    @Override
    public String getAttribute() {
        return source;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public DistanceValue getValue() {
        return () -> distance;
    }

    @Override
    public String toString() {
        return "DistanceSensorEvent{" +
                "source='" + source + '\'' +
                ", distance=" + distance +
                '}';
    }
}
