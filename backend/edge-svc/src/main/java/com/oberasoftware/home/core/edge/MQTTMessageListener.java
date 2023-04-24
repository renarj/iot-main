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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${states.producer.topic:}")
    private String eventProductTopic;

    @EventSubscribe
    public void receive(MQTTMessage message) {
        LOG.debug("Received a MQTT message: {}", message);
        ParsedPath parsedPath = MQTTPathParser.parsePath(message.getTopic());

        try {
            ValueTransportMessage parsedMessage = mapFromJson(message.getMessage(), ValueTransportMessage.class);
            if (validateMessage(parsedPath, parsedMessage)) {
                LOG.debug("Message is valid, forwarding to Inner Queues: {}", message.getMessage());

                topicSender.publish(eventProductTopic, message.getMessage());
            } else {
                LOG.warn("Message is invalid: {}", message.getMessage());
            }
        } catch(Exception e) {
            LOG.error("Fatal error deserializing, ignoring so we can continue processing state messages: {}", e.getMessage());
            LOG.debug("Full stacktrace", e);
        }
    }

    private boolean validateMessage(ParsedPath path, ValueTransportMessage value) {
        //TODO: Implement proper validation to prevent illegal messages etc.
        return true;
    }
}
