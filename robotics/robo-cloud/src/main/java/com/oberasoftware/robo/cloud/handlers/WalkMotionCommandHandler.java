package com.oberasoftware.robo.cloud.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import com.oberasoftware.iot.core.robotics.MotionEngine;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.motion.WalkDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;

/**
 * @author Renze de Vries
 */
@Component
public class WalkMotionCommandHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WalkMotionCommandHandler.class);

    @Autowired
    private RobotRegistry robotRegistry;

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "motion", label = "walk")
    public void convert(MQTTMessage mqttMessage) {
        LOG.debug("Executing Walk: {} from topic: {}", mqttMessage.getMessage(), mqttMessage.getTopic());

        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        String walkDirection = basicCommand.getAttributes().get("direction");
        WalkDirection direction = WalkDirection.FORWARD;
        if(walkDirection != null) {
            direction = WalkDirection.fromString(walkDirection);
        } else {
            LOG.warn("Received walk command, but direction not specified, assuming forward");
        }

        LOG.info("Walking in direction: {}", direction);
        RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
        MotionEngine motionEngine = robot.getMotionEngine();

        if(direction != WalkDirection.STOP) {
            motionEngine.walk(direction);
        } else {
            motionEngine.stopWalking();
        }
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "motion", label = "prepare")
    public void convertPrepareWalk(MQTTMessage mqttMessage) {
        LOG.debug("Executing Prepare Walk: {} from topic: {}", mqttMessage.getMessage(), mqttMessage.getTopic());
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
        MotionEngine motionEngine = robot.getMotionEngine();
        motionEngine.prepareWalk();
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "motion", label = "rest")
    public void convertRest(MQTTMessage mqttMessage) {
        LOG.debug("Executing Rest command: {} from topic: {}", mqttMessage.getMessage(), mqttMessage.getTopic());
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
        MotionEngine motionEngine = robot.getMotionEngine();
        motionEngine.rest();
    }

}
