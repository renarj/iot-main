package com.oberasoftware.home.core.state;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.iot.activemq.QueueConfiguration;
import com.oberasoftware.iot.activemq.RabbitMQTopicListener;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.ValueTransportMessage;
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
@Import({StateConfiguration.class, BaseConfiguration.class, QueueConfiguration.class})
public class StateContainer {
    private static final Logger LOG = LoggerFactory.getLogger(StateContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting state service");
        SpringApplication.run(StateContainer.class);
    }

    @Bean
    ApplicationRunner runStateSvc(@Autowired RabbitMQTopicListener topicListener, @Autowired StateManager stateManager, @Value("${states.consumer.topic}") String topic) {
        return args -> {
            topicListener.connect();

            topicListener.register(topic, received -> {
                ValueTransportMessage message = mapFromJson(received, ValueTransportMessage.class);
                LOG.debug("Received value: {}", message);

                message.getValues().forEach((k, v) -> {
                    stateManager.updateItemState(message.getControllerId(),
                            message.getThingId(), k, v);
                });
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Killing the RMQ connection gracefully on shutdown");
                topicListener.close();
            }));

            LOG.info("State service started");
        };
    }
}