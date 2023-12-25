package com.oberasoftware.iot.command;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.iot.activemq.QueueConfiguration;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.CoreConfiguation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Renze de Vries
 */
@SpringBootApplication
@Import({QueueConfiguration.class, BaseConfiguration.class, CoreConfiguation.class})
public class CommandContainer {
    private static final Logger LOG = LoggerFactory.getLogger(CommandContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting command channel service");
        new SpringApplication(CommandContainer.class).run(args);
    }

    @Bean
    ApplicationRunner run (@Autowired RabbitMQTopicSender sender) {
        return args -> {
            LOG.info("Connecting to Topic Sender");
            sender.connect();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Killing the sender queue gracefully on shutdown");
                sender.close();
            }));
        };
    }
}
