package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;

public interface Head extends JointChain {
    @JsonIgnore
    Joint getPitch();

    @JsonIgnore
    Joint getYaw();
}
