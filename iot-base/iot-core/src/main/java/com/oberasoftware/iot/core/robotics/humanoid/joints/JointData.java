package com.oberasoftware.iot.core.robotics.humanoid.joints;


import com.oberasoftware.iot.core.robotics.events.RobotValueEvent;

public interface JointData extends RobotValueEvent {
    String getControllerId();

    String getId();

    int getDegrees();

    int getPosition();

    int getTorgueState();
}
