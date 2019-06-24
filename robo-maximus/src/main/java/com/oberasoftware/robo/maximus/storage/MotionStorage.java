package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;

public interface MotionStorage {
    void storeKeyFrame(KeyFrame keyFrame);

    void storeMotion(Motion motion);
}
