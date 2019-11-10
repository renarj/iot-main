package com.oberasoftware.robo.maximus.rest;

import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.MotionImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController()
@RequestMapping("/editor")
public class MotionRestController {
    private static final Logger LOG = getLogger(MotionRestController.class);

    @Autowired
    private MotionStorage motionStorage;

    @RequestMapping(value = "/motion/{motionId}", method = RequestMethod.POST)
    public @ResponseBody Motion storeMotion(@PathVariable String motionId, @RequestBody MotionImpl motion) {
        LOG.info("Motion Name: {} with keyframes: {}", motion.getName(), motion.getKeyFrames());

        motionStorage.storeMotion(motionId, motion);

        return motion;
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
