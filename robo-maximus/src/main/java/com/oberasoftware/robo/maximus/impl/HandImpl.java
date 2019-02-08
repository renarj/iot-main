package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.behavioural.humanoid.Hand;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;

public class HandImpl implements Hand {
    private final String name;
    private final Joint claw;

    public HandImpl(String name, Joint claw) {
        this.name = name;
        this.claw = claw;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getJointType() {
        return null;
    }

    @Override
    public boolean moveTo(int degrees) {
        return false;
    }
}
