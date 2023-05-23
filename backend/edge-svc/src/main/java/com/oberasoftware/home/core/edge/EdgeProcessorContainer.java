package com.oberasoftware.home.core.edge;

import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import com.oberasoftware.iot.activemq.ActiveMQConfiguration;
import com.oberasoftware.iot.activemq.RabbitMQTopicListener;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;

/**
 * @author Renze de Vries
 */
@SpringBootApplication
@Import({MQTTConfiguration.class, ActiveMQConfiguration.class})
public class EdgeProcessorContainer {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeProcessorContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting edge processor");
        SpringApplication.run(EdgeProcessorContainer.class);
    }

    @Bean
    ApplicationRunner runEdge(@Autowired MQTTTopicEventBus topicEventBus, @Autowired RabbitMQTopicListener topicListener,
                              @Autowired RabbitMQTopicSender topicSender, @Autowired MQTTMessageListener messageListener,
                              @Value("${command.consumer.topic}") String commandTopic) {
        return args -> {
            LOG.info("Connecting to command channel");
            topicEventBus.initialize();
            topicEventBus.connect();
            topicListener.connect();

            topicListener.register(commandTopic, message -> {
                BasicCommandImpl basicCommand = mapFromJson(message, BasicCommandImpl.class);
                LOG.info("Received basic command: {}", basicCommand);
                topicEventBus.publish(basicCommand);
            });

            LOG.info("Connecting to topic sender for forwarding states");
            topicSender.connect();

            LOG.info("Starting listening to state changes on MQTT");
            topicEventBus.registerHandler(messageListener);
            topicEventBus.subscribe("states/#");

            LOG.info("Edge Processor started");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Killing the sender queue gracefully on shutdown");
                topicSender.close();
            }));
        };
    }
}
