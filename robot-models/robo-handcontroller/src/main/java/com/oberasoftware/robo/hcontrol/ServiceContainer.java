package com.oberasoftware.robo.hcontrol;

import com.oberasoftware.iot.client.ClientConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
@Import(ClientConfiguration.class)
public class ServiceContainer {
    private static final Logger LOG = getLogger(ServiceContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Handheld Controller");

        SpringApplication application = new SpringApplication(ServiceContainer.class);
//        application.setWebEnvironment(false);
        application.run();

        LOG.info("Handheld controller started");
    }
}
