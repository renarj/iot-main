package com.oberasoftware.home.service;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.home.rest.RestConfiguration;
import com.oberasoftware.home.rules.RuleConfiguration;
import com.oberasoftware.home.storage.StorageConfiguration;
import com.oberasoftware.home.web.WebConfiguration;
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
@Import({RestConfiguration.class, StorageConfiguration.class, CoreConfiguation.class, WebConfiguration.class, BaseConfiguration.class, RuleConfiguration.class, ClientConfiguration.class})
public class HomeAutomation {
    private static final Logger LOG = getLogger(HomeAutomation.class);

    public HomeAutomation() {

    }

    public void start(String[] args) {
        LOG.info("Starting HomeAutomation system");

        try {
            SpringApplication.run(HomeAutomation.class);
            LOG.info("HomeAutomation Service started");
        } catch (RuntimeIOTException e) {
            LOG.error("Could not start the HomeAutomationSystem", e);
        }
    }

    public static void main(String[] args) {
        HomeAutomation automation = new HomeAutomation();
        automation.start(args);
    }

}
