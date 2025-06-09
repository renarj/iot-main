package com.oberasoftware.robo.maximus.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@ConditionalOnBean(AgentClient.class)
public class ThingBasedMotionStorageImpl implements MotionStorage {
    private static final Logger LOG = getLogger(ThingBasedMotionStorageImpl.class);

    @Autowired
    private AgentClient agentClient;

    @Override
    public List<Motion> findAllMotions(String controllerId, String robotId) {
        return List.of();
    }

    @Override
    public Motion findMotion(String controllerId, String motionId) {
        try {
            var oThing = agentClient.getThing(controllerId, motionId);
            return oThing.map(t -> {
                MotionImpl m = new MotionImpl();
                m.setName(motionId);
                m.setFrames(Lists.newArrayList(fromBlob(t.getProperty("motionData"))));
                return m;
            }).orElseThrow(() -> new RuntimeIOTException("Could not load Motion"));
        } catch (IOTException e) {
            LOG.error("Could not retrieve motion with id: {} on controller: {} for reason: {}", motionId, controllerId, e.getMessage());
        }
        return null;
    }

    private KeyFrameImpl[] fromBlob(String blob) {
        LOG.info("Raw blob from storage: {}", blob);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(blob, KeyFrameImpl[].class);
        } catch (IOException e) {
            throw new RoboException("Could not load the motion from json", e);
        }
    }
}
