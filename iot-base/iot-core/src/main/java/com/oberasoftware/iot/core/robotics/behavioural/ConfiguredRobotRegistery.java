package com.oberasoftware.iot.core.robotics.behavioural;

import com.oberasoftware.iot.core.robotics.humanoid.ConfigurableRobot;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface ConfiguredRobotRegistery {
    void register(ConfigurableRobot robot);

    List<ConfigurableRobot> getRobots();

    Optional<ConfigurableRobot> getRobot(String robotId);

    Optional<ConfigurableRobot> findRobotForJoint(String controllerId, String jointId);

    Optional<ConfigurableRobot> findRobotForWheel(String controllerId, String wheelId);
}
