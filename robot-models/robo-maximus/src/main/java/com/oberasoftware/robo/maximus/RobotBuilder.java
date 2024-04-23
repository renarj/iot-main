package com.oberasoftware.robo.maximus;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;

public interface RobotBuilder {
    Robot build(RobotHardware hardware);
}
