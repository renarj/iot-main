package com.oberasoftware.robo.maximus;

import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RobotExtension implements AutomationExtension {
    private static final Logger LOG = LoggerFactory.getLogger(RobotExtension.class);

    @Override
    public String getId() {
        return "RobotExtension";
    }

    @Override
    public String getName() {
        return "Robotics Framework";
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return null;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void activate(IotThing pluginThing) {

    }

    @Override
    public void discoverThings(DiscoveryListener listener) {

    }
}
