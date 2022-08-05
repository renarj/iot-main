package com.oberasoftware.robo.cloud.motion.controllers;

import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.motion.controller.MotionController;

/**
 * @author Renze de Vries
 */
public interface RemoteController extends MotionController {
    void activate(Robot robot);
}
