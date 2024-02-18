package com.oberasoftware.iot.integrations.shelly;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.commands.impl.ConfigUpdatedCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.util.IntUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ShellyCommandHandler implements ThingCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ShellyCommandHandler.class);

    @Autowired
    private ShellyConnectorFactory connectorFactory;

    @Autowired
    private ShellyExtension shellyExtension;

    @Autowired
    private ShellyRegister register;

    @Override
    public void receive(IotThing thing, Command command) {
        if(command instanceof ConfigUpdatedCommand) {
            if(thing.getProperties().containsKey(ShellyExtension.SHELLY_IP)) {
                shellyExtension.activateShellyDevice(thing);
            }
        } else if (command instanceof SwitchCommand) {
            
        }
    }

    private void handleRelaySwitch(SwitchCommand command) {
        command.getAttributes().forEach((k, v) -> {
            if(StringUtils.hasText(k) && k.contains("relays") && k.contains("ison")) {
                int relayNr = determineRelayNumber(k);
                var metadata = register.findShelly(command.getControllerId(), command.getThingId());
                metadata.ifPresent(m -> {
                    var connector = connectorFactory.getConnector(m);
                    try {
                        connector.setRelay(m.getIp(), relayNr, command.getState(k));
                    } catch (IOTException e) {
                        LOG.error("Could not switch relay on shelly device: " + m);
                    }
                });
            }
        });
    }

    private int determineRelayNumber(String attribute) {
        if(attribute.length() > 7) {
            String stripped = attribute.substring(6, 7);
            return IntUtils.toInt(stripped, 0);
        }

        return -1;
    }
}
