package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

import java.util.List;

public interface ChainSet extends JointChain {
    List<JointChain> getJointChains();
}
