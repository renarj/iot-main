package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.humanoid.components.Head;
import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

import java.util.List;

public class HeadImpl implements Head {

    private final String name;
    private final Joint pitch;
    private final Joint yaw;

    public HeadImpl(String name, Joint pitch, Joint yaw) {
        this.name = name;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public List<JointChain> getJointChains() {
        return Lists.newArrayList();
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        return Lists.newArrayList(pitch, yaw);
    }

    @Override
    public Joint getPitch() {
        return pitch;
    }

    @Override
    public Joint getYaw() {
        return yaw;
    }

    @Override
    public String getName() {
        return name;
    }
}
