package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.iot.core.robotics.Capability;
import com.oberasoftware.iot.core.robotics.motion.Motion;

import java.util.List;

public interface MotionStorage extends Capability {
    List<Motion> findAllMotions(String controllerId, String robotId);

    Motion findMotion(String controllerId, String motionId);
}
