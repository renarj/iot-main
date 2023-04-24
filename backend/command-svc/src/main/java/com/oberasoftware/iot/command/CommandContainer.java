package com.oberasoftware.iot.command;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.iot.activemq.ActiveMQConfiguration;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Renze de Vries
 */
@SpringBootApplication
@Import({ActiveMQConfiguration.class, BaseConfiguration.class})
public class CommandContainer {
    private static final Logger LOG = LoggerFactory.getLogger(CommandContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting command channel service");
        ApplicationContext context = new SpringApplication(CommandContainer.class).run(args);
    }

    @Bean
    ApplicationRunner run (@Autowired RabbitMQTopicSender sender) {
        return args -> {
            LOG.info("Connecting to Topic Sender");
            sender.connect();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Killing the kafka connection gracefully on shutdown");
                sender.close();
            }));
        };
    }
}
