package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

public interface Ankle extends JointChain {
    Joint getAnkleX();

    Joint getAnkleY();
}
