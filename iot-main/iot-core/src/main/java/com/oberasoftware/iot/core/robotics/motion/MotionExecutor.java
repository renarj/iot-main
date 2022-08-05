package com.oberasoftware.iot.core.robotics.motion;


import com.oberasoftware.iot.core.robotics.MotionTask;

/**
 * @author Renze de Vries
 */
public interface MotionExecutor {
    MotionTask execute(Motion motion);

    MotionTask execute(KeyFrame keyFrame);
}
