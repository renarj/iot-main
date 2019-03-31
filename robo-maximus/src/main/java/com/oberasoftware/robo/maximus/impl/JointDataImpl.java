package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.behavioural.humanoid.JointData;

public class JointDataImpl implements JointData {
    private final String jointId;
    private final int desired;
    private final int actual;

    public JointDataImpl(String jointId, int desired, int actual) {
        this.jointId = jointId;
        this.desired = desired;
        this.actual = actual;
    }


    @Override
    public String getId() {
        return jointId;
    }

    @Override
    public int getActualPosition() {
        return actual;
    }

    @Override
    public int getRequestedPosition() {
        return desired;
    }

    @Override
    public String toString() {
        return "JointDataImpl{" +
                "jointId='" + jointId + '\'' +
                ", desired=" + desired +
                ", actual=" + actual +
                '}';
    }
}
