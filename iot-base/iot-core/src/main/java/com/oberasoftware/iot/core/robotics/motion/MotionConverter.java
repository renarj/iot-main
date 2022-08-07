package com.oberasoftware.iot.core.robotics.motion;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface MotionConverter {
    List<Motion> loadMotions(String motionFile);
}
