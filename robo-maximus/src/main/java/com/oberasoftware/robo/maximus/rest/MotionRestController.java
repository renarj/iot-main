package com.oberasoftware.robo.maximus.rest;

import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;
import com.oberasoftware.robo.api.motion.Motion;
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


    @RequestMapping(value = "/motion/{motionId}", method = RequestMethod.POST)
    public @ResponseBody Motion storeMotion(@PathVariable String motionId, @RequestBody MotionImpl motion) {
        LOG.info("Motion Name: {} with keyframes: {}", motion.getName(), motion.getFrames());

        motionStorage.storeMotion(motionId, motion);

        return motion;
    }

    @RequestMapping(value = "/motion/run/{robotId}/{motionId}", method = RequestMethod.POST)
    public ResponseEntity<String> runMotion(@PathVariable String robotId, @PathVariable String motionId) {
        LOG.info("Requesting motion: {} to be run on robot: {}", motionId, robotId);

        Optional<BehaviouralRobot> br = behaviouralRobotRegistry.getRobot(robotId);
        br.ifPresent(behaviouralRobot -> behaviouralRobot.getBehaviour(MotionControl.class).runMotion(motionId));

        if(br.isPresent()) {
            return new ResponseEntity<>("Motion executed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not execute motion", HttpStatus.NOT_FOUND);
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
