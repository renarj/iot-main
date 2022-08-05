package com.oberasoftware.iot.core.robotics;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface RobotRegistry {
    Robot getRobot(String name);

    List<Robot> getRobots();

    void register(Robot robot);
}
