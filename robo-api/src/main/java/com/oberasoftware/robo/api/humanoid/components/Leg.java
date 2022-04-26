package com.oberasoftware.robo.api.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

public interface Leg extends JointChain {
    @JsonIgnore
    Hip getHip();

    @JsonIgnore
    Joint getKnee();

    @JsonIgnore
    Ankle getAnkle();
}
