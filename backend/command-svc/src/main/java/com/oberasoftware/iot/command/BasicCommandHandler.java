package com.oberasoftware.iot.command;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.commands.BasicCommand;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapToJson;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class BasicCommandHandler implements EventHandler {
    private static final Logger LOG = getLogger(BasicCommandHandler.class);

    @Autowired
    private RabbitMQTopicSender topicSender;

    @Value("${command.producer.topic:}")
    private String commandTopic;

    @EventSubscribe
    public void receive(BasicCommand basicCommand) {
        LOG.info("Received a basic command: {} sending to topic: {}", basicCommand, commandTopic);

        topicSender.publish(commandTopic, mapToJson(basicCommand));
    }
}
