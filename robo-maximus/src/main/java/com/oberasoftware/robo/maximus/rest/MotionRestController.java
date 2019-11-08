package com.oberasoftware.robo.maximus.rest;

import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.MotionImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/motions")
public class MotionRestController {
    @Autowired
    private MotionStorage motionStorage;

    @RequestMapping(value = "/{motionId}", method = RequestMethod.POST)
    public Motion storeMotion(@PathVariable String motionId, MotionImpl motion) {
        motionStorage.storeMotion(motionId, motion);

        return motion;
    }

    @RequestMapping(value = "/")
    public List<Motion> getMotions() {
        return motionStorage.findAllMotions();
    }
}
