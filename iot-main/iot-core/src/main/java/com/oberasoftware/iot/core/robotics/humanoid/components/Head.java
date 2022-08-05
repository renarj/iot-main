package com.oberasoftware.iot.core.robotics.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;

public interface Head extends ChainSet {
    @JsonIgnore
    Joint getPitch();

    @JsonIgnore
    Joint getYaw();
}
