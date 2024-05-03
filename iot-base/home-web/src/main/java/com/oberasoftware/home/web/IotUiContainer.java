package com.oberasoftware.home.web;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.home.rules.RuleConfiguration;
import com.oberasoftware.home.storage.StorageConfiguration;
import com.oberasoftware.iot.client.ClientConfiguration;
import com.oberasoftware.iot.core.CoreConfiguation;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class})
@Import({StorageConfiguration.class, CoreConfiguation.class, WebConfiguration.class, BaseConfiguration.class, RuleConfiguration.class, ClientConfiguration.class})
public class IotUiContainer {
    private static final Logger LOG = getLogger(IotUiContainer.class);

    public IotUiContainer() {

    }

    public void start(String[] args) {
        LOG.info("Starting HomeAutomation UI");

        try {
            SpringApplication.run(IotUiContainer.class);
            LOG.info("HomeAutomation UI started");
        } catch (RuntimeIOTException e) {
            LOG.error("Could not start the HomeAutomation UI", e);
        }
    }

    public static void main(String[] args) {
        IotUiContainer automation = new IotUiContainer();
        automation.start(args);
    }

}
