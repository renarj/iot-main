package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.behavioural.humanoid.JointData;

public class JointDataImpl implements JointData {
    private final String jointId;
    private final int degrees;
    private final int position;

    public JointDataImpl(String jointId, int degrees, int position) {
        this.jointId = jointId;
        this.position = position;
        this.degrees = degrees;
    }

    @Override
    public int getDegrees() {
        return degrees;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getId() {
        return jointId;
    }

    @Override
    public String toString() {
        return "JointDataImpl{" +
                "jointId='" + jointId + '\'' +
                ", degrees=" + degrees +
                ", position=" + position +
                '}';
    }
}
