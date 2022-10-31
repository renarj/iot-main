package com.oberasoftware.iot.integrations.youless;

import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class YoulessExtension implements AutomationExtension {
    private static final Logger LOG = getLogger(YoulessExtension.class);

    @Autowired
    private AgentControllerInformation agentControllerInformation;
    @Autowired
    private YoulessConnector connector;

    @Override
    public void activate(IotThing pluginThing) {
        LOG.debug("Activating youless extension");

        connector.connect();
    }

    @Override
    public void discoverThings(DiscoveryListener listener) {
        listener.thingFound(ThingBuilder.create(connector.getYoulessIp(), agentControllerInformation.getControllerId())
                .addAttributes("power", "energy")
                .friendlyName("Youless Energy Monitor")
                .plugin("Youless")
                .parent("youless")
                .build());
    }
    @Override
    public String getId() {
        return "youless";
    }

    @Override
    public String getName() {
        return "Youless Plugin";
    }

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    @Override
    public CommandHandler getCommandHandler() {
        return null;
    }

    @Override
    public boolean isReady() {
        return true;
    }


}
