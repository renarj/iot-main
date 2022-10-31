package com.oberasoftware.home.core.edge;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPathParser;
import com.oberasoftware.home.core.mqtt.ParsedPath;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.model.ValueTransportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;

/**
 * @author Renze de Vries
 */
@Component
public class MQTTMessageListener implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTMessageListener.class);

    @Autowired
    private RabbitMQTopicSender topicSender;

    @EventSubscribe
    public void receive(MQTTMessage message) {
        LOG.info("Received a MQTT message: {}", message);
        ParsedPath parsedPath = MQTTPathParser.parsePath(message.getTopic());

        ValueTransportMessage parsedMessage = mapFromJson(message.getMessage(), ValueTransportMessage.class);
        if(validateMessage(parsedPath, parsedMessage)) {
            LOG.info("Message is valid, forwarding to Inner Queues: {}", message.getMessage());

            topicSender.publish(message.getMessage());
        } else {
            LOG.warn("Message is invalid: {}", message.getMessage());
        }
    }

    private boolean validateMessage(ParsedPath path, ValueTransportMessage value) {
        //TODO: Implement proper validation to prevent illegal messages etc.
        return true;
    }
}
