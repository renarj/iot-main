package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

public interface Leg extends JointChain {
    @JsonIgnore
    Hip getHip();

    @JsonIgnore
    Joint getKnee();

    @JsonIgnore
    Ankle getAnkle();
}
