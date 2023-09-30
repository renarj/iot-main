package com.oberasoftware.iot.integrations.shelly;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.commands.impl.ConfigUpdatedCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellyCommandHandler implements ThingCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ShellyCommandHandler.class);

    @Autowired
    private ShellyStatusSync shellyStatusSync;

    @Autowired
    private ShellyConnector shellyConnector;

    @Override
    public void receive(IotThing thing, Command command) {
        if(command instanceof ConfigUpdatedCommand) {
            if(thing.getProperties().containsKey(ShellyExtension.SHELLY_IP)) {
                var shellyIp = thing.getProperty(ShellyExtension.SHELLY_IP);
                if(!shellyStatusSync.isTracking(shellyIp)) {
                    LOG.info("Config update for new Shelly received: {}, fetching and syncing", thing);
                    try {
                        var metadata = shellyConnector.getShellyInfo(thing.getControllerId(), thing.getThingId(), shellyIp);
                        shellyStatusSync.addShelly(metadata);
                    } catch (IOTException e) {
                        LOG.error("Could not retrieve Shelly metadata for tracking", e);
                    }
                } else {
                    LOG.info("Shelly: {} already syncing, skipping config update", shellyIp);
                }
            }
        }
    }
}
