package com.oberasoftware.robo.maximus.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.robotics.behavioural.JointBasedRobotRegistery;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.MotionEngine;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class MotionHandler implements RobotAttributeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MotionHandler.class);

    @Autowired
    private JointBasedRobotRegistery robotRegistry;


    @Override
    public Set<String> getSupportedAttributes() {
        return Set.of("frame", "motion");
    }

    @Override
    public void handle(String attribute, ThingValueCommand command) {
        if(command.getAttributes().containsKey("frame")) {
            gotoKeyFrame(command);
        } else if(command.getAttributes().containsKey("motion")) {
            runMotion(command);
        }
    }

    private void runMotion(ThingValueCommand command) {
        String motionId = command.getAttribute("motion").asString();
        var oRobot = robotRegistry.getRobot(command.getThingId());
        oRobot.ifPresent(r -> {
            LOG.info("Execution motion: {} on robot: {}", motionId, oRobot);
            var jc = r.getBehaviour(JointControl.class);
            jc.runMotion(command.getControllerId(), motionId);
        });
    }

    private void gotoKeyFrame(ThingValueCommand command) {
        String keyFrame = StringEscapeUtils.unescapeJson(command.getAttribute("frame").asString());
        ObjectMapper mapper = new ObjectMapper();
        LOG.info("Received key frame: {} attempting to deserialize", keyFrame);
        try {
            var parsedFrame = mapper.readValue(keyFrame, KeyFrameImpl.class);
            LOG.info("Parsed frames: {}", parsedFrame);

            Motion motion = new MotionImpl("tempMotion", Lists.newArrayList(parsedFrame));
            Optional<JointBasedRobot> br = robotRegistry.getRobot(command.getThingId());
            br.ifPresent(behaviouralRobot -> behaviouralRobot.getBehaviour(MotionEngine.class).post(motion));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
