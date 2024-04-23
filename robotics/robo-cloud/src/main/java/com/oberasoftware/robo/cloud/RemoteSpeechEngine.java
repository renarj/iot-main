package com.oberasoftware.robo.cloud;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.SpeechEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.iot.core.commands.BasicCommandBuilder.create;


@Component
@Scope("prototype")
public class RemoteSpeechEngine implements SpeechEngine {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteSpeechEngine.class);

    private RobotHardware robot;

    @Override
    public void say(String text, String language) {
        BasicCommand command = create(robot.getName())
                .thing("tts")
                .attribute("say")
                .type(CommandType.SET_STATE)
                .property("text", text)
                .property("language", language)
                .build();

        LOG.info("Doing a remote TTS operation: {}", command);
        robot.getRemoteDriver().publish(command);
    }

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        LOG.info("Activating remote speech engine for robot: {}", robot.getName());
        this.robot = robot;
    }

    @Override
    public void shutdown() {

    }
}
