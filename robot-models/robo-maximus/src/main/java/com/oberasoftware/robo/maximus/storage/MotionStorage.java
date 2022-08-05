package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.iot.core.robotics.Capability;
import com.oberasoftware.iot.core.robotics.motion.Motion;

import java.util.List;

public interface MotionStorage extends Capability {
    void storeMotion(String motionName, Motion motion);

    void deleteMotion(String motionName);

    List<Motion> findAllMotions();

    Motion findMotion(String motionName);
}
