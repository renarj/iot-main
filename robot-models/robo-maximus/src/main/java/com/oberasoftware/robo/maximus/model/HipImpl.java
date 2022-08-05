package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.humanoid.components.Hip;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

import java.util.List;

public class HipImpl implements Hip {

    private final String name;
    private final Joint x;
    private final Joint y;
    private final Joint z;

    public HipImpl(String name, Joint x, Joint y, Joint z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Joint getXJoint() {
        return x;
    }

    @Override
    public Joint getYJoint() {
        return y;
    }

    @Override
    public Joint getZJoint() {
        return z;
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        return Lists.newArrayList(x, y, z);
    }

    @Override
    public String getName() {
        return name;
    }
}
