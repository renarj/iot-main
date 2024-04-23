package com.oberasoftware.iot.core.robotics.behavioural;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface BehaviouralRobotRegistry {
    void register(Robot robot);

    List<Robot> getRobots();

    Optional<Robot> getRobot(String robotId);
}
