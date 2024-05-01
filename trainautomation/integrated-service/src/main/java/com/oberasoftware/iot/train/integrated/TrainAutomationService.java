package com.oberasoftware.iot.train.integrated;

import com.oberasoftware.home.core.edge.EdgeProcessorContainer;
import com.oberasoftware.home.core.state.StateContainer;
import com.oberasoftware.home.data.DataServiceContainer;
import com.oberasoftware.home.service.IotUiContainer;
import com.oberasoftware.iot.command.CommandContainer;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.train.TrainServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {SpringApplicationAdminJmxAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import({IotUiContainer.class, DataServiceContainer.class,
        CommandContainer.class,
        EdgeProcessorContainer.class, StateContainer.class,
        TrainServiceContainer.class})
//@Import({TrainServiceContainer.class, ClientConfiguration.class, JasDBConfiguration.class})
public class TrainAutomationService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainAutomationService.class);

    public static void main(String[] args) {
        LOG.info("Starting Train Automation system");

        try {
            SpringApplication.run(TrainAutomationService.class);
            LOG.info("Train Automation Service started");
        } catch (RuntimeIOTException e) {
            LOG.error("Could not start the Train Automation system", e);
        }
    }

}
