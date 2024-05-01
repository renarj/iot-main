package com.oberasoftware.robo.maximus;

public class ThingKey {
    private final String controllerId;
    private final String thingId;

    private final String servoId;

    private final String robotId;

    public ThingKey(String controllerId, String thingId, String servoId, String robotId) {
        this.controllerId = controllerId;
        this.servoId = servoId;
        this.thingId = thingId;
        this.robotId = robotId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public String getServoId() {
        return servoId;
    }

    public String getThingId() {
        return thingId;
    }

    public String getRobotId() {
        return robotId;
    }

    @Override
    public String toString() {
        return "ThingKey{" +
                "controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", robotId='" + robotId + '\'' +
                '}';
    }
}
