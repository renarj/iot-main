package com.oberasoftware.robo.core;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renze de Vries
 */
@Component
public class RobotRegistryImpl implements RobotRegistry {

    private Robot robot;

    @Override
    public Robot getRobot(String name) {
        if(robot.getName().equalsIgnoreCase(name)) {
            return robot;
        } else {
            throw new RuntimeIOTException("This is a singleton robot registry, no robot with name: " + name + " registered");
        }
    }

    @Override
    public Robot getDefaultRobot() {
        return robot;
    }

    @Override
    public List<Robot> getRobots() {
        return Lists.newArrayList(robot);
    }

    @Override
    public void register(Robot robot) {
        this.robot = robot;
    }
}
