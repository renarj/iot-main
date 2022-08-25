package com.oberasoftware.home.agent.service;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.home.agent.core.AgentCoreConfiguration;
import com.oberasoftware.home.agent.core.ExtensionServiceLoaderUtil;
import com.oberasoftware.home.api.AutomationBus;
import com.oberasoftware.home.api.exceptions.HomeAutomationException;
import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import com.oberasoftware.home.api.extensions.ExtensionManager;
import com.oberasoftware.home.api.extensions.SpringExtension;
import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Renze de Vries
 */
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import(value = {AgentCoreConfiguration.class, MQTTConfiguration.class, BaseConfiguration.class})
@ComponentScan
public class AgentService {
    private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);

    public void start(String[] args) {
        LOG.info("Starting HomeAutomation Agent");

        try {
            List<SpringExtension> springExtensions = ExtensionServiceLoaderUtil.getExtensions();
            List<Class<?>> c = springExtensions.stream().map(SpringExtension::getClass).collect(Collectors.toList());
            c.add(AgentService.class);

            LOG.debug("Starting spring context with configuration classes: {}", c);
            ApplicationContext context = SpringApplication.run(c.toArray(), args);

            String controllerId = context.getBean(AutomationBus.class).getControllerId();

            ExtensionManager extensionManager = context.getBean(ExtensionManager.class);
            extensionManager.activateController(controllerId);

            extensionManager.activateExtensions();
            LOG.info("HomeAutomation system Started and ready for duty");
        } catch (HomeAutomationException | RuntimeHomeAutomationException e) {
            LOG.error("Could not start the HomeAutomationSystem", e);
        }
    }

    public static void main(String[] args) {
        AgentService agent = new AgentService();
        agent.start(args);
    }
}
