package com.oberasoftware.home.service.events.controller;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.api.managers.ItemManager;
import com.oberasoftware.home.core.ControllerConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class PluginUpdateHandler implements EventHandler {
    private static final Logger LOG = getLogger(PluginUpdateHandler.class);

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ControllerConfiguration controllerConfiguration;

    @EventSubscribe
    public void receive(PluginUpdateEvent pluginUpdateEvent) throws HomeAutomationException {
        LOG.debug("Received a plugin update command: {}", pluginUpdateEvent);

        itemManager.createOrUpdatePlugin(controllerConfiguration.getControllerId(), pluginUpdateEvent.getPluginId(), pluginUpdateEvent.getName(), pluginUpdateEvent.getProperties());
    }
}
