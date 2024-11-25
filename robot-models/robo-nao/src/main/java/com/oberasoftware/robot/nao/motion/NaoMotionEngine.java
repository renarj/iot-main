package com.oberasoftware.robot.nao.motion;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.oberasoftware.iot.core.robotics.MotionTask;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.iot.core.robotics.motion.WalkDirection;
import com.oberasoftware.iot.core.robotics.motion.controller.MotionController;
import com.oberasoftware.robot.nao.NaoSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.oberasoftware.robot.nao.NaoUtil.safeExecuteTask;


/**
 * @author Renze de Vries
 */
@Component
public class NaoMotionEngine  {
    private static final Logger LOG = LoggerFactory.getLogger(NaoMotionEngine.class);

    @Autowired
    private NaoSessionManager sessionManager;

    private HandsMotionController handsMotionController;

    private ALMotion alMotion;
    private ALBehaviorManager behaviorManager;
    private ALRobotPosture alPosture;

    public void activate(RobotHardware robot, Map<String, String> properties) {
        try {
            Session session = sessionManager.getSession();
            alMotion = new ALMotion(session);
            alPosture = new ALRobotPosture(session);
            behaviorManager = new ALBehaviorManager(session);

            handsMotionController = new HandsMotionController(alMotion);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public void shutdown() {
        safeExecuteTask(() -> alMotion.killAll());
        safeExecuteTask(() -> behaviorManager.stopAllBehaviors());
        rest();
    }

    public <T extends MotionController> Optional<T> getMotionController(String controllerName) {
        if(controllerName.equalsIgnoreCase("hands")) {
            return Optional.of((T)handsMotionController);
        }
        return Optional.empty();
    }

    public KeyFrame getCurrentPositionAsKeyFrame() {
        return null;
    }

    public List<String> getMotions() {
        try {
            return behaviorManager.getBehaviorNames();
        } catch (CallError | InterruptedException e) {
            LOG.error("Could not load behaviours", e);
            return new ArrayList<>();
        }
    }

    public boolean prepareWalk() {
        return safeExecuteTask(() -> alPosture.goToPosture("Stand", 0.5f));
    }

    public MotionTask goToPosture(String posture) {
        safeExecuteTask(() -> alPosture.goToPosture(posture, 0.5f));

        return null;
    }

    public MotionTask walkForward() {
        return walk(WalkDirection.FORWARD);
    }

    public MotionTask walk(WalkDirection direction) {
        switch (direction) {
            case STOP:
                stopWalking();
                break;
            case LEFT:
                safeExecuteTask(() -> alMotion.move(1.0f, 1.0f, 0.0f));
                break;
            case RIGHT:
                safeExecuteTask(() -> alMotion.move(1.0f, -1.0f, 0.0f));
                break;
            case BACKWARD:
                safeExecuteTask(() -> alMotion.move(-1.0f, 0.0f, 0.0f));
                break;
            case FORWARD:
                safeExecuteTask(() -> alMotion.move(1.0f, 0.0f, 0.0f));
                break;
            default:
                safeExecuteTask(() -> alMotion.move(1.0f, 0.0f, 0.0f));
                break;
        }

        safeExecuteTask(() -> alMotion.waitUntilMoveIsFinished());

        return null;
    }

    public MotionTask walk(WalkDirection walkDirection, float i) {
        if(walkDirection == WalkDirection.FORWARD) {
            safeExecuteTask(() -> alMotion.moveTo(i, 0f, 0.0f));
        } else {
            safeExecuteTask(() -> alMotion.moveTo(-i, 0f, 0.0f));
        }

        return null;
    }

    public boolean rest() {
        return safeExecuteTask(() -> alMotion.rest());
    }

    public MotionTask runMotion(String motionName) {
        LOG.info("Executing motion: {}", motionName);
        safeExecuteTask(() -> behaviorManager.runBehavior(motionName));
        return null;
    }

    public boolean stopWalking() {
        LOG.info("Stop walking");
        return safeExecuteTask(() -> alMotion.move(0.0f, 0.0f, 0.0f));
    }

}
