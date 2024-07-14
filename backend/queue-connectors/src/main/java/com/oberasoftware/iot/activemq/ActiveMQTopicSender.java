package com.oberasoftware.iot.activemq;

import com.oberasoftware.iot.core.messaging.TopicSender;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.jms.*;

/**
 * @author Renze de Vries
 */
@Component
public class ActiveMQTopicSender implements TopicSender<String> {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQTopicSender.class);

    public static final String CONNECTION_FORMAT = "tcp://%s:61616";

    @Value("${amq.host:}")
    private String amqHost;

    @Value("${amq.producer.topic:}")
    private String amqTopic;

    private MessageProducer producer;

    private Connection connection;
    private Session session;

    @Override
    public void connect() {
        if(StringUtils.hasText(amqHost)) {
            String connectionString = String.format(CONNECTION_FORMAT, amqHost);
            LOG.info("Connecting to ActiveMQ Host: {} on topic: {}", connectionString, amqTopic);
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionString);

            try {
                connection = connectionFactory.createConnection();
                connection.start();

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(amqTopic);

                producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                LOG.info("Connected to ActiveMQ");
            } catch (JMSException e) {
                LOG.error("Could not connect to ActiveMQ", e);
            }
        } else {
            LOG.warn("No ActiveMQ Host configured");
        }
    }

    @Override
    public void close() {
        LOG.info("Disconnecting from ActiveMQ host: {}", amqHost);
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            LOG.error("Could not cleanly disconnect from ActiveMQ", e);
        }
    }

    @Override
    public void publish(String topic, String message) {
        LOG.debug("Publishing: {} to topic: {}", message, amqTopic);

        try {
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
        } catch (JMSException e) {
            LOG.error("Could not send message: {} to topic: {} for reason: {}", message, amqTopic, e.getMessage());
        }
    }
}
