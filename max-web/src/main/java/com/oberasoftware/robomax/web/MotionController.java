package com.oberasoftware.robomax.web;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.motion.MotionManager;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Renze de Vries
 */
@RestController
@RequestMapping("/motions")
public class MotionController {
    private static final Logger LOG = LoggerFactory.getLogger(MotionController.class);

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private MotionManager motionManager;

    @RequestMapping(value = "/keyframe", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public KeyFrame getKeyFrame() {
        LOG.info("Retrieving current servo positions as keyframe");
        return getDefaultRobot().getMotionEngine().getCurrentPositionAsKeyFrame();
    }

    @RequestMapping(value = "/run/{motionName}", method = RequestMethod.POST)
    public void runMotion(@PathVariable String motionName) {
        LOG.info("Triggering motion: {} execution", motionName);
        getDefaultRobot().getMotionEngine().runMotion(motionName);
    }

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Motion storeMotion(MotionImpl motion) {
        motionManager.storeMotion(motion);

        return motion;
    }

    private Robot getDefaultRobot() {
        return robotRegistry.getRobots().get(0);
    }
}
