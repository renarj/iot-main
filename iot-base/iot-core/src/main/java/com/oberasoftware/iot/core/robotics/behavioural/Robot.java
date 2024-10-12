package com.oberasoftware.iot.core.robotics.behavioural;


import java.util.List;

/**
 * @author renarj
 */
public interface Robot {
    String getRobotId();

    String getControllerId();

    List<Behaviour> getBehaviours();

    <T extends Behaviour> T getBehaviour(Class<T> behaviourClass);

}
