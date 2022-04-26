package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.humanoid.components.Leg;
import com.oberasoftware.robo.api.humanoid.components.Legs;
import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

import java.util.List;

public class LegsImpl implements Legs {
    private static final String LEGS = "Legs";
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
    public List<JointChain> getJointChains() {
        return ImmutableList.<JointChain>builder()
                .add(leftLeft)
                .add(rightLeg)
                .build();
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        if(includeChildren) {
            return ImmutableList.<Joint>builder()
                    .addAll(leftLeft.getJoints())
                    .addAll(rightLeg.getJoints())
                    .build();
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public String getName() {
        return LEGS;
    }

    @Override
    public String toString() {
        return "LegsImpl{" +
                "leftLeft=" + leftLeft +
                ", rightLeg=" + rightLeg +
                '}';
    }
}
