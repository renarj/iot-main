package com.oberasoftware.robo.cloud.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.SpeechEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;

/**
 * @author Renze de Vries
 */
@Component
public class SpeechCommandHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SpeechCommandHandler.class);

    @Autowired
    private RobotRegistry robotRegistry;

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "tts", label = "say")
    public void convert(MQTTMessage mqttMessage) {
        LOG.debug("Executing text to speech: {} from topic: {}", mqttMessage.getMessage(), mqttMessage.getTopic());
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        RobotHardware robot = robotRegistry.getRobot(basicCommand.getControllerId());
        SpeechEngine speechEngine = robot.getCapability(SpeechEngine.class);

        if(speechEngine != null) {
            String textToSpeech = basicCommand.getAttributes().get("text");
            String language = basicCommand.getAttributes().get("language");
            if(StringUtils.hasText(textToSpeech) && StringUtils.hasText(language)) {
                LOG.info("Text to speech of: {} for language: {}", textToSpeech, language);

                speechEngine.say(textToSpeech, language);
            } else {
                LOG.warn("Received text to speech command, but no text or language defined");
            }
        } else {
            LOG.warn("Text to speech called, but capability not installed");
        }
    }
}
