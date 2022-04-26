package com.oberasoftware.robo.api.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.robo.api.humanoid.joints.Joint;
import com.oberasoftware.robo.api.humanoid.joints.JointChain;

public interface Arm extends JointChain {
    @JsonIgnore
    Shoulder getShoulder();

    @JsonIgnore
    Joint getElbow();

    @JsonIgnore
    Joint getElbowRoll();

    @JsonIgnore
    Joint getHand();
}
