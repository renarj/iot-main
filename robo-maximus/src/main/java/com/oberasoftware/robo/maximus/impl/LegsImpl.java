package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.JointChain;
import com.oberasoftware.robo.api.behavioural.humanoid.Leg;
import com.oberasoftware.robo.api.behavioural.humanoid.Legs;

import java.util.List;

public class LegsImpl implements Legs {
    private final Leg leftLeft;
    private final Leg rightLeg;

    public LegsImpl(Leg leftLeft, Leg rightLeg) {
        this.leftLeft = leftLeft;
        this.rightLeg = rightLeg;
    }

    @Override
    public Leg getLeg(String legName) {
        return legName.equalsIgnoreCase("left") ? leftLeft : rightLeg;
    }

    @Override
    public List<JointChain> getChains() {
        return ImmutableList.<JointChain>builder()
                .add(leftLeft)
                .add(rightLeg)
                .build();
    }

    @Override
    public Joint getJoint(String name) {
        return null;
    }

    @Override
    public List<Joint> getJoints() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
