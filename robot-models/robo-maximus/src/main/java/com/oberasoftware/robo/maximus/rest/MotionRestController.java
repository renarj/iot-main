package com.oberasoftware.robo.maximus.rest;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.MotionEngine;
import com.oberasoftware.iot.core.robotics.humanoid.NavigationControl;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CartesianControl;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CartesianMoveInput;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@RestController()
@RequestMapping("/editor")
public class MotionRestController {
    private static final Logger LOG = getLogger(MotionRestController.class);

    private final MotionStorage motionStorage;

    private final BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Autowired
    public MotionRestController(MotionStorage motionStorage, BehaviouralRobotRegistry behaviouralRobotRegistry) {
        this.motionStorage = motionStorage;
        this.behaviouralRobotRegistry = behaviouralRobotRegistry;
    }

    @RequestMapping(value = "/motion/navigate/{robotId}")
    public ResponseEntity<String> navigate(@PathVariable String robotId, @RequestBody CartesianMoveInput moveInput) {
        LOG.info("Doing Navigational move: {} on robot: {}", moveInput, robotId);
        Optional<Robot> br = behaviouralRobotRegistry.getRobot(robotId);

        if(br.isPresent()) {
            NavigationControl navigationControl = br.map(behaviouralRobot -> behaviouralRobot.getBehaviour(NavigationControl.class)).orElseThrow();
            navigationControl.move(moveInput.getX(), moveInput.getY(), moveInput.getZ());

            return new ResponseEntity<>("Navigation move executed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not execute Navigation move", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/motion/cartesian/{robotId}")
    public ResponseEntity<String> move(@PathVariable String robotId, @RequestBody CartesianMoveInput moveInput) {
        LOG.info("Doing cartesian move: {} on robot: {}", moveInput, robotId);
        Optional<Robot> br = behaviouralRobotRegistry.getRobot(robotId);

        if(br.isPresent()) {
            CartesianControl cartesianControl = br.map(behaviouralRobot -> behaviouralRobot.getBehaviour(CartesianControl.class)).orElseThrow();
            cartesianControl.move(moveInput);

            return new ResponseEntity<>("Cartesian move executed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not execute cartesian move", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/motion/{motionId}", method = RequestMethod.POST)
    public @ResponseBody Motion storeMotion(@PathVariable String motionId, @RequestBody MotionImpl motion) {
        LOG.info("Motion Name: {} with keyframes: {}", motion.getName(), motion.getFrames());

        motionStorage.storeMotion(motionId, motion);

        return motion;
    }

    @RequestMapping(value = "/motion/run/{robotId}/{motionId}", method = RequestMethod.POST)
    public ResponseEntity<String> runMotion(@PathVariable String robotId, @PathVariable String motionId) {
        LOG.info("Requesting motion: {} to be run on robot: {}", motionId, robotId);

        Optional<Robot> br = behaviouralRobotRegistry.getRobot(robotId);
        br.ifPresent(behaviouralRobot -> behaviouralRobot.getBehaviour(JointControl.class).runMotion(motionId));

        if(br.isPresent()) {
            return new ResponseEntity<>("Motion executed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not execute motion", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/motion/run/{robotId}/keyFrame")
    public ResponseEntity<String> runKeyFrame(@PathVariable String robotId, @RequestBody KeyFrameImpl keyFrame) {
        LOG.info("Requesting keyframe: {} to be run on robot: {}", keyFrame, robotId);

        Motion motion = new MotionImpl("tempMotion", Lists.newArrayList(keyFrame));
        Optional<Robot> br = behaviouralRobotRegistry.getRobot(robotId);
        br.ifPresent(behaviouralRobot -> behaviouralRobot.getBehaviour(MotionEngine.class).post(motion));

        if(br.isPresent()) {
            return new ResponseEntity<>("Keyframe executed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not execute keyFrame, robot not found", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/motion/{motionId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMotion(@PathVariable String motionId) {
        motionStorage.deleteMotion(motionId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

        @RequestMapping(value = "/motions")
    public List<Motion> getMotions() {
        return motionStorage.findAllMotions();
    }

    @RequestMapping(value = "/motions/{motionId}")
    public Motion getMotion(@PathVariable String motionId) {
        return motionStorage.findMotion(motionId);
    }
}
