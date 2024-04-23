package com.oberasoftware.robo.cloud.motion.controllers;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.motion.controller.MotionController;

/**
 * @author Renze de Vries
 */
public interface RemoteController extends MotionController {
    void activate(RobotHardware robot);
}
