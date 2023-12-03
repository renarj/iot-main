package com.oberasoftware.home.data;

import com.oberasoftware.home.storage.jasdb.JasDBConfiguration;
import com.oberasoftware.iot.activemq.QueueConfiguration;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.CoreConfiguation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class})
@Import({CoreConfiguation.class, JasDBConfiguration.class, QueueConfiguration.class})
public class DataServiceContainer {
    private static final Logger LOG = getLogger( DataServiceContainer.class );

    public static void main(String[] args) {
        LOG.info("Starting Core Data Services");
        new SpringApplication(DataServiceContainer.class).run(args);
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
