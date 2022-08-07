package com.oberasoftware.iot.core.robotics.behavioural;


import com.oberasoftware.iot.core.robotics.Robot;

/**
 * @author renarj
 */
public interface Behaviour {
    default void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {

    }
}
