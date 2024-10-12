package com.oberasoftware.robo.maximus.model;

import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

import java.util.List;

public class BasicJointChain implements JointChain {
    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        return List.of();
    }

    @Override
    public String getName() {
        return "";
    }
}
