package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.robo.api.Capability;
import com.oberasoftware.robo.api.motion.Motion;

import java.util.List;

public interface MotionStorage extends Capability {
    void storeMotion(String motionName, Motion motion);

    void deleteMotion(String motionName);

    List<Motion> findAllMotions();

    Motion findMotion(String motionName);
}
