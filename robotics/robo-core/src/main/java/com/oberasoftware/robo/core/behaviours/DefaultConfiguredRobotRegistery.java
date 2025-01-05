package com.oberasoftware.robo.core.behaviours;

import com.oberasoftware.iot.core.robotics.behavioural.ConfiguredRobotRegistery;
import com.oberasoftware.iot.core.robotics.humanoid.ConfigurableRobot;
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
public class DefaultConfiguredRobotRegistery implements ConfiguredRobotRegistery {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConfiguredRobotRegistery.class);

    private final List<ConfigurableRobot> robots = new CopyOnWriteArrayList<>();

    @Override
    public void register(ConfigurableRobot robot) {
        robots.add(robot);
    }

    @Override
    public List<ConfigurableRobot> getRobots() {
        return new ArrayList<>(robots);
    }

    @Override
    public Optional<ConfigurableRobot> getRobot(String robotId) {
        return robots.stream()
                .filter(r -> r.getRobotId().equalsIgnoreCase(robotId))
                .findFirst();
    }

    @Override
    public Optional<ConfigurableRobot> findRobotForJoint(String controllerId, String jointId) {
        AtomicReference<ConfigurableRobot> foundRobot = new AtomicReference<>();
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
    }

    @Override
    public Optional<ConfigurableRobot> findRobotForWheel(String controllerId, String wheelId) {
        AtomicReference<ConfigurableRobot> foundRobot = new AtomicReference<>();
        robots.forEach(r -> {
            LOG.info("Checking robot: {}", r.getRobotId());

            r.getWheels().forEach(w -> {
                LOG.info("Checking wheel: {}", w.getThingId());

                if(w.getThingId().equalsIgnoreCase(wheelId)) {
                    LOG.info("Found robot: {}", r.getRobotId());
                    foundRobot.set(r);
                }
            });
        });
        return Optional.ofNullable(foundRobot.get());    }
}
