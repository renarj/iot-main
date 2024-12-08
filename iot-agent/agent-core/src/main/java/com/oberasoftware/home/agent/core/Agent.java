package com.oberasoftware.home.agent.core;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.home.agent.core.extension.ExtensionServiceLoaderUtil;
import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import com.oberasoftware.home.rules.RuleConfiguration;
import com.oberasoftware.iot.client.ClientConfiguration;
import com.oberasoftware.iot.core.CoreConfiguation;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.extensions.SpringExtension;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@Import({CoreConfiguation.class, ClientConfiguration.class, BaseConfiguration.class, AgentWebConfiguration.class, MQTTConfiguration.class, RuleConfiguration.class})
public class Agent {
    private static final Logger LOG = getLogger( Agent.class );

    public static void main(String[] args) {
        var agent = new Agent();
        agent.start(args);
    }

    public void start(String[] args) {
        LOG.info("Starting IoT Agent");

        try {
            List<SpringExtension> springExtensions = ExtensionServiceLoaderUtil.getExtensions();
            List<Class<?>> c = springExtensions.stream().map(SpringExtension::getClass).collect(Collectors.toList());
            c.add(Agent.class);

            Class<?>[] ar = new Class[c.size()];
            for(int i=0; i<c.size(); i++) {
                ar[i] = c.get(i);
            }

            LOG.debug("Starting spring context with configuration classes: {}", c);
            ApplicationContext context = SpringApplication.run(ar, args);
            AgentBootstrap bootstrap = context.getBean(AgentBootstrapImpl.class);
            bootstrap.startAgent();
        } catch (IOTException | RuntimeIOTException e) {
            LOG.error("Could not start the IoT Agent", e);
            System.exit(-1);
        }
    }


}
