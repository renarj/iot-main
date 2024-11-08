package com.oberasoftware.iot.activemq;

import com.oberasoftware.iot.core.messaging.TopicConsumer;
import com.oberasoftware.iot.core.messaging.TopicListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.jms.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * This is the Active MQ Topic listener.
 *
 * @author Renze de Vries
 */
@Component
public class ActiveMQTopicListener implements TopicListener<String>, ExceptionListener {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQTopicListener.class);

    private static final int AMQ_READ_TIMEOUT = 100;
    private static final int POLLER_SLEEP_INTERVAL = 1000;

    @Value("${amq.host:}")
    private String amqHost;

    @Value("${amq.consumer.topic:}")
    private String amqTopic;

    private volatile boolean running;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<TopicConsumer<String>> topicConsumers = new CopyOnWriteArrayList<>();

    private MessageConsumer consumer;
    private Session session;
    private Connection connection;

    @Override
    public void connect() {
        String connectionString = String.format(ActiveMQTopicSender.CONNECTION_FORMAT, amqHost);
        LOG.info("Connecting to ActiveMQ Host: {} on topic: {}", connectionString, amqTopic);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionString);

        if(!StringUtils.isEmpty(connectionString)) {
            try {
                connection = connectionFactory.createConnection();
                connection.start();
                connection.setExceptionListener(this);

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(amqTopic);

                consumer = session.createConsumer(destination);

                running = true;
                executorService.submit(this::runPoller);
                LOG.info("Finished connect to ActiveMQ host: {}", amqHost);
            } catch (JMSException e) {
                LOG.error("", e);
            }
        } else {
            LOG.warn("No ActiveMQ host configured, cannot connect");
        }
    }

    @Override
    public void close() {
        LOG.info("Closing ActiveMQ connection to: {}", amqHost);
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);

            session.close();
            connection.close();
        } catch (InterruptedException e) {
            LOG.error("TopicListener thread not shutdown cleanly: {}", e.getMessage());
        } catch (JMSException e) {
            LOG.error("Could not cleanly disconnect from ActiveMQ", e);
        }
        LOG.info("Closing consumer");


    }

    @Override
    public void register(String topic, TopicConsumer<String> topicConsumer) {
        LOG.info("Registering topicConsumer: {}", topicConsumer);
        topicConsumers.add(topicConsumer);
    }

    private void runPoller() {
        LOG.info("Poller has started");
        while(running && !Thread.currentThread().isInterrupted()) {
            try {
                Message message = consumer.receive(AMQ_READ_TIMEOUT);
                if(message != null) {
                    if (message instanceof TextMessage textMessage) {
                        LOG.info("Received text message: {}", textMessage.getText());
                        notifyListeners(textMessage.getText());
                    } else {
                        LOG.warn("Received unrecognized message: {}", message);
                    }
                } else {
                    sleepUninterruptibly(POLLER_SLEEP_INTERVAL, MILLISECONDS);
                }
            } catch (JMSException e) {
                LOG.error("Could not read message from ActiveMQ", e);
            }
        }
        LOG.info("Poller has stopped");
    }

    private void notifyListeners(String message) {
        topicConsumers.forEach(c -> {
            LOG.info("Notifying consumer: {} with message: {}", c, message);
            c.receive(message);
        });
    }

    @Override
    public void onException(JMSException exception) {
        LOG.error("Received JMS error: {}", exception);
    }
}
