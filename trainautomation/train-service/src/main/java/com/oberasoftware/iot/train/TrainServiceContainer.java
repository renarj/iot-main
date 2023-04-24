package com.oberasoftware.iot.train;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TrainServiceContainer {
    private static final Logger LOG = LoggerFactory.getLogger(TrainServiceContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Train Storage service");
        ApplicationContext context = new SpringApplication(TrainServiceContainer.class).run(args);

    }
}
