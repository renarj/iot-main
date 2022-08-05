package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.iot.core.robotics.humanoid.components.Ankle;
import com.oberasoftware.iot.core.robotics.humanoid.components.Hip;
import com.oberasoftware.iot.core.robotics.humanoid.components.Leg;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

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
