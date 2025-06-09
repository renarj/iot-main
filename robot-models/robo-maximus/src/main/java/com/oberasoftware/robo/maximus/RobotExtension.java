package com.oberasoftware.robo.maximus;

import com.google.common.collect.Maps;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.handlers.RobotCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnBean(AgentClient.class)
public class RobotExtension implements AutomationExtension {
    private static final Logger LOG = LoggerFactory.getLogger(RobotExtension.class);
    public static final String ROBOT_EXTENSION = "RobotExtension";

    @Autowired
    private IotRobotInitializer robotInitializer;

    @Autowired
    private RobotCommandHandler commandHandler;

    @Override
    public String getId() {
        return ROBOT_EXTENSION;
    }

    @Override
    public String getName() {
        return "Robotics Framework";
    }

    @Override
    public Map<String, String> getProperties() {
        return Maps.newHashMap();
    }

    @Override
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void activate(IotThing pluginThing) {
        LOG.info("Starting to activate Robot system");
        robotInitializer.initialize();
    }

    @Override
    public void discoverThings(DiscoveryListener listener) {

    }
}
