package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JasDBMotionStorageImpl implements MotionStorage {
    private static final Logger LOG = getLogger(JasDBMotionStorageImpl.class);

    @Override
    public void storeKeyFrame(KeyFrame keyFrame) {

    }

    @Override
    public void storeMotion(Motion motion) {

    }
}
