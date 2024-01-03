package com.oberasoftware.iot.integrations.shelly;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.commands.impl.ConfigUpdatedCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
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
    private ShellyExtension shellyExtension;

    @Override
    public void receive(IotThing thing, Command command) {
        if(command instanceof ConfigUpdatedCommand) {
            if(thing.getProperties().containsKey(ShellyExtension.SHELLY_IP)) {
                shellyExtension.activateShellyDevice(thing);
            }
        } else if (command instanceof ValueCommandImpl) {
            
        }
    }
}
