package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

public interface Shoulder extends JointChain {
    Joint getXJoint();

    Joint getYJoint();

    Joint getZJoint();
}
