package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainCommandHandler implements ThingCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TrainCommandHandler.class);

    @Autowired
    private CommandCenterFactory commandCenterFactory;

    @Autowired
    private ThingClient thingClient;

    @Override
    public void receive(IotThing item, Command command) {
        LOG.info("Received a train command: {} for thing: {}", command, item);
        var commandCenterId = item.getParentId();

        try {
            var oCommandCenter = thingClient.getThing(item.getControllerId(), commandCenterId);

            oCommandCenter.ifPresentOrElse(ct -> {
                var commandCenterType = ct.getProperty(TrainAutomationExtension.COMMAND_CENTER_TYPE);
                commandCenterFactory.getCommandCenter(commandCenterType).ifPresentOrElse(c -> {
                    LOG.info("We will do an action on command center: {}", c);

                    c.handleCommand((ItemValueCommand) command);
                }, () -> {
                    LOG.error("Could not find command center with id: {} for command: {}", commandCenterType, command);
                });

            }, () -> {
                LOG.error("Could not find the Thing information on the command center: {} for item: {}", commandCenterId, item);
            });
        } catch (IOTException e) {
            LOG.error("Could not retrieve command center information", e);
        }
    }
}
