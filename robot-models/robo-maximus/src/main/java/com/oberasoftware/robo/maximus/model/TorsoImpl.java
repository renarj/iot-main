package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.humanoid.components.Arm;
import com.oberasoftware.iot.core.robotics.humanoid.components.Torso;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

import java.util.List;

public class TorsoImpl implements Torso {

    public static final String TORSO = "Torso";

    private final Arm leftArm;
    private final Arm rightArm;

    public TorsoImpl(Arm leftArm, Arm rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public List<Joint> getJoints() {
        return getJoints(false);
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        if(includeChildren) {
            return ImmutableList.<Joint>builder()
                    .addAll(leftArm.getJoints())
                    .addAll(rightArm.getJoints())
                    .build();
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public String getName() {
        return TORSO;
    }
}
