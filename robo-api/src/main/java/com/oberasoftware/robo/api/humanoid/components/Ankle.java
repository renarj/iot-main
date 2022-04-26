package com.oberasoftware.robo.api.humanoid.components;

import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

public interface Ankle extends JointChain {
    Joint getAnkleX();

    Joint getAnkleY();
}
