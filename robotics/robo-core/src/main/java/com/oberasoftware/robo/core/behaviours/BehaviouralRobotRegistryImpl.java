package com.oberasoftware.robo.core.behaviours;

import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobotRegistry;
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

    private final List<Robot> robots = new CopyOnWriteArrayList<>();

//    @Autowired(required = false)
//    private RemoteCloudDriver remoteCloudDriver;

    @Override
    public void register(Robot robot) {
        robots.add(robot);

//        if(remoteCloudDriver != null) {
//            new ControllerPersistCommand();
//        }
    }

    @Override
    public List<Robot> getRobots() {
        return new ArrayList<>(robots);
    }

    @Override
    public Optional<Robot> getRobot(String robotId) {
        return robots.stream()
                .filter(r -> r.getRobotId().equalsIgnoreCase(robotId))
                .findFirst();
    }
}
