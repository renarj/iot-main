package com.oberasoftware.robo.hexapod;

import java.util.Map;

public class RobotFeedback {
    private final String robotId;
    private final PosData direction;
    private final PosData rotation;

    private final Map<String, AngleData> legAngles;

    public RobotFeedback(String robotId, PosData direction, PosData rotation, Map<String, AngleData> legAngles) {
        this.robotId = robotId;
        this.direction = direction;
        this.rotation = rotation;
        this.legAngles = legAngles;
    }

    public Map<String, AngleData> getLegAngles() {
        return legAngles;
    }

    public String getRobotId() {
        return robotId;
    }

    public PosData getDirection() {
        return direction;
    }

    public PosData getRotation() {
        return rotation;
    }
}
