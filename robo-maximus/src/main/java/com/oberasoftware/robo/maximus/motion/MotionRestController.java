package com.oberasoftware.robo.maximus.motion;

import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/motions")
public class MotionRestController {
    @Autowired
    private MotionStorage motionStorage;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Motion createMotion(MotionImpl motion) {
        motionStorage.storeMotion(motion);

        return motion;
    }

    @RequestMapping(value = "/frame", method = RequestMethod.POST)
    public KeyFrame createKeyFrame(KeyFrameImpl keyFrame) {
        motionStorage.storeKeyFrame(keyFrame);

        return keyFrame;
    }
}
