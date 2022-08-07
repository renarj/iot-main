package com.oberasoftware.iot.core.robotics.behavioural;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface BehaviouralRobotRegistry {
    void register(BehaviouralRobot robot);

    List<BehaviouralRobot> getRobots();

    Optional<BehaviouralRobot> getRobot(String robotId);
}
