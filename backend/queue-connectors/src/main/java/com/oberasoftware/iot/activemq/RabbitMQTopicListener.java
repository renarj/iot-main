package com.oberasoftware.iot.activemq;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.messaging.TopicConsumer;
import com.oberasoftware.iot.core.messaging.TopicListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RabbitMQTopicListener extends AbstractRMQConnector implements TopicListener<String> {
    private static final Logger LOG = getLogger( RabbitMQTopicListener.class );

    @Value("${rmq.host:}")
    private String rmqHost;

    @Value("${rmq.consumer.topic:}")
    private String rmqTopic;

    @Value("${rmq.port}")
    private Integer rmqPort;

    private List<TopicConsumer<String>> topicConsumers = new CopyOnWriteArrayList<>();


    @Override
    public void connect() {
        super.connect(rmqHost, rmqPort, rmqTopic);

        try {
            var queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, rmqTopic, "");

            channel.basicConsume(queueName, true, (consumerTag, message) -> {
                var msg = new String(message.getBody(), 0, message.getBody().length, Charset.defaultCharset());
                LOG.info("Received RMQ message: {} from topic: {}", msg, rmqTopic);
                notifyListeners(msg);
            }, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeIOTException("Unable to bind to queue on RMQ: " + rmqHost, e);
        }
    }

    @Override
    public void close() {
        super.close(rmqHost);
    }

    @Override
    public void register(TopicConsumer<String> topicConsumer) {
        LOG.info("Registering topicConsumer: {}", topicConsumer);
        topicConsumers.add(topicConsumer);
    }


    private void notifyListeners(String message) {
        topicConsumers.forEach(c -> {
            LOG.info("Notifying consumer: {} with message: {}", c, message);
            c.receive(message);
        });
    }
}
