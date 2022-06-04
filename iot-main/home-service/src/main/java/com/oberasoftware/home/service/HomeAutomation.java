package com.oberasoftware.home.service;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.home.api.exceptions.HomeAutomationException;
import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import com.oberasoftware.home.api.extensions.ExtensionManager;
import com.oberasoftware.home.api.extensions.SpringExtension;
import com.oberasoftware.home.core.ControllerConfiguration;
import com.oberasoftware.home.core.CoreConfiguation;
import com.oberasoftware.home.rest.RestConfiguration;
import com.oberasoftware.home.rules.RuleConfiguration;
import com.oberasoftware.home.storage.jasdb.JasDBConfiguration;
import com.oberasoftware.home.web.WebConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class})
@Import({RestConfiguration.class, JasDBConfiguration.class, CoreConfiguation.class, WebConfiguration.class, BaseConfiguration.class, RuleConfiguration.class})
public class HomeAutomation {
    private static final Logger LOG = getLogger(HomeAutomation.class);

    public HomeAutomation() {

    }

    public void start(String[] args) {
        LOG.info("Starting HomeAutomation system");

        try {
            List<SpringExtension> springExtensions = ExtensionServiceLoaderUtil.getExtensions();
            List<Class<?>> c = springExtensions.stream().map(SpringExtension::getClass).collect(Collectors.toList());
            c.add(HomeAutomation.class);

            Class<?>[] ar = new Class[c.size()];
            for(int i=0; i<c.size(); i++) {
                ar[i] = c.get(i);
            }

            LOG.debug("Starting spring context with configuration classes: {}", c);
            ApplicationContext context = SpringApplication.run(ar, args);

            String controllerId = context.getBean(ControllerConfiguration.class).getControllerId();

            ExtensionManager extensionManager = context.getBean(ExtensionManager.class);
            extensionManager.activateController(controllerId);

            extensionManager.activateExtensions();
            LOG.info("HomeAutomation system Started and ready for duty");
        } catch (HomeAutomationException | RuntimeHomeAutomationException e) {
            LOG.error("Could not start the HomeAutomationSystem", e);
        }
    }

    public static void main(String[] args) {
        HomeAutomation automation = new HomeAutomation();
        automation.start(args);
    }

}
