package com.oberasoftware.robo.maximus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.robo.core.motion.JointTargetImpl;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class SimpleJsonStructureTest {
    private static final Logger LOG = getLogger(SimpleJsonStructureTest.class);

    public static void main(String[] args) throws Exception {
        KeyFrameImpl keyFrame1 = new KeyFrameImpl("Raise", 2000);
        keyFrame1.addServoStep(new JointTargetImpl("124", 2029));
        keyFrame1.addServoStep(new JointTargetImpl("135", 2029));

        KeyFrameImpl keyFrame2 = new KeyFrameImpl("Lower", 1000);
        keyFrame2.addServoStep(new JointTargetImpl("124", 20));
        keyFrame2.addServoStep(new JointTargetImpl("135", 20));


        List<KeyFrame> keyFrames = new ArrayList<>();
        keyFrames.add(keyFrame1);
        keyFrames.add(keyFrame2);
        MotionImpl motion = new MotionImpl("raise Hand", keyFrames);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(motion);
        LOG.info("JSON Structure: {}", json);

        Motion loadedMotion = mapper.readValue(json, MotionImpl.class);

        LOG.info("Motion Name: {} with keyframes: {}", loadedMotion.getName(), loadedMotion.getFrames());
    }
}
