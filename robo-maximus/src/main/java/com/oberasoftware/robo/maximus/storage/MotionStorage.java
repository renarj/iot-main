package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.robo.api.motion.Motion;

import java.util.List;

public interface MotionStorage {
    void storeMotion(String motionId, Motion motion);

    List<Motion> findAllMotions();

    Motion findMotion(String motionId);
}
