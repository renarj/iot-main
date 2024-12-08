package com.oberasoftware.robo.core.behaviours;

import com.oberasoftware.iot.core.robotics.behavioural.JointBasedRobotRegistery;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author renarj
 */
@Component
public class DefaultJointBasedRobotRegistery implements JointBasedRobotRegistery {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJointBasedRobotRegistery.class);

    private final List<JointBasedRobot> robots = new CopyOnWriteArrayList<>();

    @Override
    public void register(JointBasedRobot robot) {
        robots.add(robot);
    }

    @Override
    public List<JointBasedRobot> getRobots() {
        return new ArrayList<>(robots);
    }

    @Override
    public Optional<JointBasedRobot> getRobot(String robotId) {
        return robots.stream()
                .filter(r -> r.getRobotId().equalsIgnoreCase(robotId))
                .findFirst();
    }

    @Override
    public Optional<JointBasedRobot> findRobotForJoint(String controllerId, String jointId) {
        AtomicReference<JointBasedRobot> foundRobot = new AtomicReference<>();
        robots.forEach(r -> {
            LOG.info("Checking robot: {}", r.getRobotId());

            r.getJoints().forEach(j -> {
                LOG.info("Checking joint: {}", j.getJointId());

                if(j.getJointId().equalsIgnoreCase(jointId)) {
                    LOG.info("Found robot: {}", r.getRobotId());
                    foundRobot.set(r);
                }
            });
        });
        return Optional.ofNullable(foundRobot.get());
//        return robots.stream().filter(r -> r.getJoint(jointId).isPresent()).findFirst();
    }
}
