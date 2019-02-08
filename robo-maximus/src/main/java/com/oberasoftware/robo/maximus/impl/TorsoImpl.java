package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.robo.api.behavioural.humanoid.Arm;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.JointChain;
import com.oberasoftware.robo.api.behavioural.humanoid.Torso;

import java.util.List;

public class TorsoImpl implements Torso {

    private static final String TORSO = "Torso";

    private final Arm leftArm;
    private final Arm rightArm;

    public TorsoImpl(Arm leftArm, Arm rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public List<JointChain> getChains() {
        return ImmutableList.<JointChain>builder()
                .add(leftArm, rightArm).build();
    }

    @Override
    public List<Joint> getJoints() {
        return ImmutableList.<Joint>builder()
                .addAll(leftArm.getJoints())
                .addAll(rightArm.getJoints())
                .build();
    }

    @Override
    public String getName() {
        return TORSO;
    }
}
