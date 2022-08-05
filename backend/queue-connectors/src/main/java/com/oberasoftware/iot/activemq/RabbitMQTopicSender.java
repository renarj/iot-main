package com.oberasoftware.iot.activemq;

import com.oberasoftware.iot.core.messaging.TopicSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RabbitMQTopicSender extends AbstractRMQConnector implements TopicSender<String> {
    private static final Logger LOG = getLogger( RabbitMQTopicSender.class );

    @Value("${rmq.host:}")
    private String rmqHost;

    @Value("${rmq.port}")
    private Integer rmqPort;

    @Value("${rmq.producer.topic:}")
    private String rmqTopic;

    @Override
    public void connect() {
        super.connect(rmqHost, rmqPort, rmqTopic);
    }

    @Override
    public void close() {
        super.close(rmqHost);
    }

    @Override
    public void publish(String message) {
        LOG.debug("Publishing: {} to topic: {}", message, rmqTopic);

        try {
            channel.basicPublish(rmqTopic, "", null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error("Unable to send message to RMQ: " + rmqHost + " on topic: " + rmqTopic, e);
        }

    }
}
