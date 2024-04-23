package com.oberasoftware.robo.core;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renze de Vries
 */
@Component
public class RobotRegistryImpl implements RobotRegistry {

    private RobotHardware robot;

    @Override
    public RobotHardware getRobot(String name) {
        if(robot.getName().equalsIgnoreCase(name)) {
            return robot;
        } else {
            throw new RuntimeIOTException("This is a singleton robot registry, no robot with name: " + name + " registered");
        }
    }

    @Override
    public RobotHardware getDefaultRobot() {
        return robot;
    }

    @Override
    public List<RobotHardware> getRobots() {
        return Lists.newArrayList(robot);
    }

    @Override
    public void register(RobotHardware robot) {
        this.robot = robot;
    }
}
