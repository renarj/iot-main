package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.humanoid.components.Ankle;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

import java.util.List;

public class AnkleImpl implements Ankle {

    private final String name;
    private final Joint x;
    private final Joint y;

    public AnkleImpl(String name, Joint x, Joint y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public Joint getAnkleX() {
        return x;
    }

    @Override
    public Joint getAnkleY() {
        return y;
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
        return "AnkleImpl{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
