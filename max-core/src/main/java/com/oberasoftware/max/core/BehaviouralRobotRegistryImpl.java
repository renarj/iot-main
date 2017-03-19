package com.oberasoftware.max.core;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author renarj
 */
@Component
public class BehaviouralRobotRegistryImpl implements BehaviouralRobotRegistry {

    private List<BehaviouralRobot> robots = new CopyOnWriteArrayList<>();

    @Override
    public void register(BehaviouralRobot robot) {
        robots.add(robot);
    }

    @Override
    public List<BehaviouralRobot> getRobots() {
        return new ArrayList<>(robots);
    }

    @Override
    public Optional<BehaviouralRobot> getRobot(String robotId) {
        return robots.stream()
                .filter(r -> r.getRobotId().equalsIgnoreCase(robotId))
                .findFirst();
    }
}
