package com.oberasoftware.home.core.state;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.iot.activemq.ActiveMQConfiguration;
import com.oberasoftware.iot.activemq.ActiveMQTopicListener;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.ValueTransportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;


/**
 * @author Renze de Vries
 */
@SpringBootApplication
@Import({StateConfiguration.class, BaseConfiguration.class, ActiveMQConfiguration.class})
public class StateContainer {
    private static final Logger LOG = LoggerFactory.getLogger(StateContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting state service");
        ApplicationContext context = SpringApplication.run(StateContainer.class);
        ActiveMQTopicListener topicListener = context.getBean(ActiveMQTopicListener.class);
        topicListener.connect();
        StateManager stateManager = context.getBean(StateManager.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the kafka gracefully on shutdown");
            topicListener.close();
        }));

        topicListener.register(received -> {
            try {
                ValueTransportMessage message = mapFromJson(received, ValueTransportMessage.class);
                LOG.debug("Received value: {}", message);
                stateManager.setState(message.getControllerId(),
                        message.getChannelId(), message.getLabel(), message.getValue());
            } catch(Exception e) {
                LOG.error("Fatal error, ignoring so we can continue processing state messages", e);
            }
        });

    }
}
