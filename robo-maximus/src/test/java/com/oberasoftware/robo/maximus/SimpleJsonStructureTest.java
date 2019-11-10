package com.oberasoftware.robo.maximus;

import com.google.gson.Gson;
import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import com.oberasoftware.robo.core.motion.ServoStepImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class SimpleJsonStructureTest {
    private static final Logger LOG = getLogger(SimpleJsonStructureTest.class);

    public static void main(String[] args) {
        KeyFrameImpl keyFrame1 = new KeyFrameImpl("Raise", 2000);
        keyFrame1.addServoStep(new ServoStepImpl("124", 2029));
        keyFrame1.addServoStep(new ServoStepImpl("135", 2029));

        KeyFrameImpl keyFrame2 = new KeyFrameImpl("Lower", 1000);
        keyFrame2.addServoStep(new ServoStepImpl("124", 20));
        keyFrame2.addServoStep(new ServoStepImpl("135", 20));


        List<KeyFrame> keyFrames = new ArrayList<>();
        keyFrames.add(keyFrame1);
        keyFrames.add(keyFrame2);
        MotionImpl motion = new MotionImpl("raise Hand", keyFrames);

        String json = new Gson().toJson(motion);
        LOG.info("JSON Structure: {}", json);

        Motion loadedMotion = new Gson().fromJson(json, MotionImpl.class);

        LOG.info("Motion Name: {} with keyframes: {}", loadedMotion.getName(), loadedMotion.getKeyFrames());
    }
}
