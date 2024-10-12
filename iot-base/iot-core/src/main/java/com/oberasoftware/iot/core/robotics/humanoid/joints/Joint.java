package com.oberasoftware.iot.core.robotics.humanoid.joints;


import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;

public interface Joint extends Behaviour {
    String getControllerId();

    String getRobotId();

    String getJointId();

    String getServoId();

    String getName();

    boolean isInverted();

    String getJointType();

    int getMaxDegrees();

    int getMinDegrees();

    boolean moveTo(int degrees);
}
