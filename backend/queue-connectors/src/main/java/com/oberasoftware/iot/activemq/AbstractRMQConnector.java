package com.oberasoftware.iot.activemq;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractRMQConnector {
    private static final Logger LOG = getLogger( AbstractRMQConnector.class );

    protected Connection connection;
    protected Channel channel;

    protected void connect(String rmqHost, int rmqPort, String rmqTopic) {
        if(StringUtils.hasText(rmqHost)) {
            var factory = new ConnectionFactory();
            factory.setHost(rmqHost);
            factory.setPort(rmqPort);
            try {
                LOG.info("Connecting to RabbitMQ Host: {}:{} on topic: {}", rmqHost, rmqPort, rmqTopic);
                this.connection = factory.newConnection();
                this.channel = connection.createChannel();

                for (String topic : rmqTopic.split(",")) {
                    LOG.info("Registering topic: {} to RMQ", topic);
                    this.channel.exchangeDeclare(topic, "fanout");
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeIOTException("Could not connect to RabbitMQ on Host: " + rmqHost, e);
            }
        } else {
            LOG.warn("No RabbitMQ host is configured");
        }
    }

    protected void close(String rmqHost) {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeIOTException("Could not cleanly close the RMQ connection to host: " + rmqHost, e);
        }
    }
}
