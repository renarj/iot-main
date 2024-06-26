package com.oberasoftware.robo.cloud.motion;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.robotics.MotionEngine;
import com.oberasoftware.iot.core.robotics.MotionTask;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.iot.core.robotics.motion.MotionResource;
import com.oberasoftware.iot.core.robotics.motion.WalkDirection;
import com.oberasoftware.iot.core.robotics.motion.controller.MotionController;
import com.oberasoftware.robo.cloud.motion.controllers.RemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.oberasoftware.iot.core.commands.BasicCommandBuilder.create;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Renze de Vries
 */
@Component
@Scope("prototype")
public class RemoteMotionEngine implements MotionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteMotionEngine.class);

    private RobotHardware robot;

    @Autowired
    private List<RemoteController> remoteControllers;

    @Override
    public boolean prepareWalk() {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("motion", "stand")
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean rest() {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("motion", "rest")
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public <T extends MotionController> Optional<T> getMotionController(String controllerName) {
        Optional<RemoteController> remoteController = remoteControllers.stream()
                .filter(r -> r.getName().equalsIgnoreCase(controllerName)).findFirst();
        if(remoteController.isPresent()) {
            return of((T)remoteController.get());
        } else {
            return empty();
        }
    }

    @Override
    public List<String> getMotions() {
        return null;
    }

    @Override
    public void loadResource(MotionResource resource) {

    }

    @Override
    public KeyFrame getCurrentPositionAsKeyFrame() {
        return null;
    }

    @Override
    public MotionTask walkForward() {
        return walk(WalkDirection.FORWARD);
    }

    @Override
    public MotionTask walk(WalkDirection direction) {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("direction", direction.name())
                .property("motion", "walk")
                .build();

        robot.getRemoteDriver().publish(command);

        return null;
    }

    @Override
    public MotionTask walk(WalkDirection direction, float meters) {
        return walk(WalkDirection.FORWARD);
    }

    @Override
    public MotionTask runMotion(String motionName) {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("motion", "run")
                .property("motion", motionName)
                .build();

        robot.getRemoteDriver().publish(command);
        return null;
    }

    @Override
    public MotionTask runMotion(Motion motion) {
        return null;
    }

    @Override
    public MotionTask runMotion(KeyFrame keyFrame) {
        return null;
    }

    @Override
    public MotionTask goToPosture(String posture) {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("posture", posture)
                .build();

        robot.getRemoteDriver().publish(command);

        return null;
    }

    @Override
    public List<MotionTask> getActiveTasks() {
        return null;
    }

    @Override
    public boolean stopTask(MotionTask task) {
        return false;
    }

    @Override
    public boolean stopAllTasks() {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .property("motion", "stop")
                .build();
        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean stopWalking() {
        walk(WalkDirection.STOP);

        return false;
    }

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        LOG.info("Activating remote motion engine for robot: {}", robot.getName());
        this.robot = robot;

        remoteControllers.forEach(rc -> rc.activate(robot));
    }

    @Override
    public void shutdown() {
        LOG.info("Doing shutdown of robot");
        rest();
    }
}
