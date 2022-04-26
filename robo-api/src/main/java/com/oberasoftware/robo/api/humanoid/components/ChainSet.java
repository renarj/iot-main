package com.oberasoftware.robo.api.humanoid.components;

import com.oberasoftware.robo.api.humanoid.joints.JointChain;

import java.util.List;

public interface ChainSet extends JointChain {
    List<JointChain> getJointChains();
}
