package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.robo.api.behavioural.humanoid.Ankle;
import com.oberasoftware.robo.api.behavioural.humanoid.Hip;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.Leg;

import java.util.List;

public class LegImpl implements Leg {

    private final String name;
    private final Hip hip;
    private final Joint knee;
    private final Ankle ankle;

    public LegImpl(String name, Hip hip, Joint knee, Ankle ankle) {
        this.name = name;
        this.hip = hip;
        this.knee = knee;
        this.ankle = ankle;
    }

    @Override
    public Hip getHip() {
        return hip;
    }

    @Override
    public Joint getKnee() {
        return knee;
    }

    @Override
    public Ankle getAnkle() {
        return ankle;
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        ImmutableList.Builder<Joint> b = ImmutableList.<Joint>builder()
                .add(knee);

        b.addAll(hip.getJoints());
        b.addAll(ankle.getJoints());

        return b.build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LegImpl{" +
                "name='" + name + '\'' +
                ", hip=" + hip +
                ", knee=" + knee +
                ", ankle=" + ankle +
                '}';
    }
}
