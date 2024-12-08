package com.oberasoftware.iot.core.robotics.behavioural;

import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface JointBasedRobotRegistery {
    void register(JointBasedRobot robot);

    List<JointBasedRobot> getRobots();

    Optional<JointBasedRobot> getRobot(String robotId);

    Optional<JointBasedRobot> findRobotForJoint(String controllerId, String jointId);
}
