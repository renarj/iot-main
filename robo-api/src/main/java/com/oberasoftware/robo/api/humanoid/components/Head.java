package com.oberasoftware.robo.api.humanoid.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.robo.api.humanoid.joints.Joint;

public interface Head extends ChainSet {
    @JsonIgnore
    Joint getPitch();

    @JsonIgnore
    Joint getYaw();
}
