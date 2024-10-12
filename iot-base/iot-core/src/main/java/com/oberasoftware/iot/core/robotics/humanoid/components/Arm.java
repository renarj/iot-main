package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

public interface Arm extends JointChain {
    @JsonIgnore
    Shoulder getShoulder();

    @JsonIgnore
    Joint getElbow();

    @JsonIgnore
    Joint getHand();
}
