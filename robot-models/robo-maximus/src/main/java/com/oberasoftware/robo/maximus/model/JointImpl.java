package com.oberasoftware.robo.maximus.model;

import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

public class JointImpl implements Joint {

    public static final int DEFAULT_MAX_DEGREES = 180;
    public static final int DEFAULT_MIN_DEGREES = -180;

    private final String controllerId;
    private final String robotId;
    private final String thingId;
    private final String servoId;
    private final String name;
    private final String type;
    private final boolean inverted;

    private final int minDegrees;
    private final int maxDegrees;

    public JointImpl(String robotId, String controllerId, String thingId, String servoId, String name, String type, boolean inverted) {
        this.robotId = robotId;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.servoId = servoId;
        this.name = name;
        this.type = type;
        this.minDegrees = DEFAULT_MIN_DEGREES;
        this.maxDegrees = DEFAULT_MAX_DEGREES;
        this.inverted = inverted;
    }

    public JointImpl(String robotId, String controllerId, String thingId, String servoId, String name, String type, int minDegrees, int maxDegrees, boolean inverted) {
        this.robotId = robotId;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.servoId = servoId;
        this.name = name;
        this.type = type;
        this.minDegrees = minDegrees;
        this.maxDegrees = maxDegrees;
        this.inverted = inverted;
    }

    @Override
    public String getRobotId() {
        return this.robotId;
    }

    @Override
    public boolean isInverted() {
        return inverted;
    }

    @Override
    public int getMaxDegrees() {
        return maxDegrees;
    }

    @Override
    public int getMinDegrees() {
        return minDegrees;
    }

    @Override
    public String getJointId() {
        return thingId;
    }

    @Override
    public String getServoId() {
        return servoId;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getJointType() {
        return type;
    }

    @Override
    public boolean moveTo(int degrees) {
        return false;
    }
}
