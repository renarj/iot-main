package com.oberasoftware.max.web;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@SpringBootApplication
@Import(WebConfiguration.class)
public class RobotDashboardContainer {
    private static final Logger LOG = getLogger(RobotDashboardContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting robot dashboard");

        SpringApplication.run(RobotDashboardContainer.class, args);
    }
}
