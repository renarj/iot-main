package com.oberasoftware.iot.activemq;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.messaging.TopicConsumer;
import com.oberasoftware.iot.core.messaging.TopicListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ListMultimap<String, TopicConsumer<String>> topicConsumers =
            Multimaps.newListMultimap(new ConcurrentHashMap<>(), ArrayList::new);

    @Override
    public void connect() {
        super.connect(rmqHost, rmqPort, rmqTopic);
    }

    @Override
    public void close() {
        super.close(rmqHost);
    }

    @Override
    public void register(String topic, TopicConsumer<String> topicConsumer) {
        try {
            var queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");

            channel.basicConsume(queueName, true, (consumerTag, message) -> {
                var msg = new String(message.getBody(), Charset.defaultCharset());
                LOG.debug("Received RMQ message: {} from topic: {}", msg, rmqTopic);
                notifyListeners(message.getEnvelope().getExchange(), msg);
            }, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeIOTException("Unable to bind to queue on RMQ: " + rmqHost, e);
        }


        LOG.info("Registering topicConsumer: {}", topicConsumer);
        topicConsumers.put(topic, topicConsumer);
    }


    private void notifyListeners(String topic, String message) {
        topicConsumers.get(topic).forEach(c -> {
            LOG.debug("Notifying topic: {} consumer: {} with message: {}", topic, c, message);
            c.receive(message);
        });
    }
}
