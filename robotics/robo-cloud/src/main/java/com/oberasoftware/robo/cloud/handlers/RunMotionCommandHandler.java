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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;

/**
 * @author Renze de Vries
 */
@Component
public class RunMotionCommandHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RunMotionCommandHandler.class);

    @Autowired
    private RobotRegistry robotRegistry;

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "motion", label = "run")
    public void convert(MQTTMessage mqttMessage) {
        LOG.info("Executing motion: {} from topic: {}", mqttMessage.getMessage(), mqttMessage.getTopic());
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        String motionName = basicCommand.getAttributes().get("motion");
        if(motionName != null) {
            LOG.info("Received motion execution: {}", motionName);

            RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
            MotionEngine motionEngine = robot.getMotionEngine();
            motionEngine.runMotion(motionName);
        } else {
            LOG.warn("Received motion command, but motion not specified");
        }
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "motion", label = "stop")
    public void receiveStopCommand(MQTTMessage mqttMessage) {
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);
        LOG.info("Stopping all motion tasks for controller: {}", basicCommand.getControllerId());

        RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
        MotionEngine motionEngine = robot.getMotionEngine();
        motionEngine.stopAllTasks();
    }
}
