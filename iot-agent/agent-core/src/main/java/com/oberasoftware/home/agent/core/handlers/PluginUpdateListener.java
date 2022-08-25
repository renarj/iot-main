package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.events.impl.PluginUpdateEvent;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class PluginUpdateListener implements EventHandler {
    private static final Logger LOG = getLogger( PluginUpdateListener.class );

    @Autowired
    private ThingClient thingClient;

    @EventSubscribe
    public void receive(PluginUpdateEvent event) throws IOTException {
        LOG.info("Received plugin update: {} storing changes", event);

        thingClient.createOrUpdate(new IotThingImpl(event.getControllerId(), event.getPluginId(),
                event.getName(), event.getPluginId(), null, event.getProperties()));
    }
}
