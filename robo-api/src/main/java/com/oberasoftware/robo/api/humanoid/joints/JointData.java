package com.oberasoftware.robo.api.humanoid.joints;

import com.oberasoftware.robo.api.events.RobotValueEvent;

public interface JointData extends RobotValueEvent {
    String getId();

    int getDegrees();

    int getPosition();

    int getTorgueState();
}
