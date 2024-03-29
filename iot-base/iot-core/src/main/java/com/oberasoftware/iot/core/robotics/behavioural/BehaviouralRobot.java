package com.oberasoftware.iot.core.robotics.behavioural;


import java.util.List;

/**
 * @author renarj
 */
public interface BehaviouralRobot {
    String getRobotId();

    List<Behaviour> getBehaviours();

    <T extends Behaviour> T getBehaviour(Class<T> behaviourClass);

}
