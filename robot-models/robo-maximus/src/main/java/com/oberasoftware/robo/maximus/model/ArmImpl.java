package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.iot.core.robotics.humanoid.components.Arm;
import com.oberasoftware.iot.core.robotics.humanoid.components.Shoulder;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

import java.util.List;

public class ArmImpl implements Arm {

    private final String name;
    private final Shoulder shoulder;
    private final Joint elbow;
    private final Joint elbowRoll;
    private final Joint hand;

    public ArmImpl(String name, Shoulder shoulder, Joint elbow, Joint elbowRoll, Joint hand) {
        this.name = name;
        this.shoulder = shoulder;
        this.elbow = elbow;
        this.elbowRoll = elbowRoll;
        this.hand = hand;
    }

    @Override
    public Shoulder getShoulder() {
        return shoulder;
    }

    @Override
    public Joint getElbow() {
        return elbow;
    }

    @Override
    public Joint getElbowRoll() {
        return elbowRoll;
    }

    @Override
    public Joint getHand() {
        return hand;
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        ImmutableList.Builder<Joint> b = ImmutableList.<Joint>builder()
                .add(hand)
                .add(elbow)
                .add(elbowRoll);

        b.addAll(shoulder.getJoints());

        return b.build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ArmImpl{" +
                "name='" + name + '\'' +
                ", shoulder=" + shoulder +
                ", elbow=" + elbow +
                ", hand=" + hand +
                '}';
    }
}
