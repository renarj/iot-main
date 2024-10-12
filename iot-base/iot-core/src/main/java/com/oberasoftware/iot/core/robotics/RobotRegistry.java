package com.oberasoftware.iot.core.robotics;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface RobotRegistry {
    RobotHardware getRobot(String name);

    boolean containsRobot(String name);

    List<RobotHardware> getRobots();

    RobotHardware getDefaultRobot();

    void register(RobotHardware robot);
}
