package com.oberasoftware.robo.api.humanoid.components;

import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

public interface Shoulder extends JointChain {
    Joint getXJoint();

    Joint getYJoint();

    Joint getZJoint();
}
