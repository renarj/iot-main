package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.humanoid.components.Shoulder;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

import java.util.List;

public class ShoulderImpl implements Shoulder {

    private final Joint x;
    private final Joint y;

    private final String name;

    public ShoulderImpl(String name, Joint x, Joint y) {
        this.x = x;
        this.y = y;
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
        return null;
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        return Lists.newArrayList(x, y);
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
                ", name='" + name + '\'' +
                '}';
    }
}
