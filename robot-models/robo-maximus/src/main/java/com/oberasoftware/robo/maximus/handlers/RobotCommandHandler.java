package com.oberasoftware.robo.maximus.handlers;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RobotCommandHandler implements ThingCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RobotCommandHandler.class);

    @Autowired
    private List<RobotAttributeHandler> handlers;

    @Override
    public void receive(IotThing thing, Command command) {
        LOG.info("Received command: {} for thing: {}", command, thing);

        switch(command) {
            case ThingValueCommand tv:
                executeAttributes(tv);
                break;
            default:
        }
    }

    private void executeAttributes(ThingValueCommand thingValueCommand) {
        thingValueCommand.getAttributes().forEach((k, v) -> {
            executeHandlers(thingValueCommand, k);
        });
    }

    private void executeHandlers(ThingValueCommand thingValueCommand, String attribute) {
        handlers.stream().filter(h -> h.getSupportedAttributes().contains(attribute)).forEach(h -> {
            LOG.info("Executing handler: {} for command: {} and attribute: {}", h, thingValueCommand, attribute);
            h.handle(attribute, thingValueCommand);
        });
    }
}
