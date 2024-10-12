package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

public interface Legs extends JointChain {
    Leg getLeg(String legName);
}
