package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.behavioural.humanoid.Head;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.JointChain;

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
