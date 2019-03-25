package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.Shoulder;

import java.util.List;

public class ShoulderImpl implements Shoulder {

    private final Joint x;
    private final Joint y;
    private final Joint z;

    private final String name;

    public ShoulderImpl(String name, Joint x, Joint y, Joint z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
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

    @Override
    public String toString() {
        return "ShoulderImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }
}
