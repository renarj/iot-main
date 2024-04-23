package com.oberasoftware.iot.core.robotics.behavioural;


import com.oberasoftware.iot.core.robotics.RobotHardware;

/**
 * @author renarj
 */
public interface Behaviour {
    default void initialize(Robot behaviouralRobot, RobotHardware robotCore) {

    }
}
