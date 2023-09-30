package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.events.ThingUpdateEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ThingUpdateHandler implements EventHandler {
    private static final Logger LOG = getLogger(ThingUpdateHandler.class);

    @Autowired
    private ThingClient thingClient;

    @EventSubscribe
    public void receive(ThingUpdateEvent event) throws Exception {
        LOG.debug("Received a Thing update for plugin: {} and device: {}", event.getPluginId(), event.getThing());
        thingClient.createOrUpdate(event.getThing());
    }
}
