package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.behavioural.humanoid.Ankle;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;

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
