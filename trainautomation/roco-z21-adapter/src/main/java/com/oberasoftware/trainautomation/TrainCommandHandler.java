package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.train.StepMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrainCommandHandler implements ThingCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TrainCommandHandler.class);

    @Autowired
    private CommandCenterFactory commandCenterFactory;

    @Autowired
    private AgentClient agentClient;

    @Override
    public void receive(IotThing item, Command command) {
        LOG.info("Received a train command: {} for thing: {}", command, item);
        var commandCenterId = item.getParentId();

        try {
            var oCommandCenter = agentClient.getThing(item.getControllerId(), commandCenterId);

            oCommandCenter.ifPresentOrElse(ct -> {
                var commandCenterType = ct.getProperty(TrainAutomationExtension.COMMAND_CENTER_TYPE);
                commandCenterFactory.getCommandCenter(commandCenterType).ifPresentOrElse(c -> {
                    LOG.info("We will do an action on command center: {}", c);

                    var enrichedCommand = translate(item, command);
                    enrichedCommand.ifPresent(c::handleCommand);
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

    private Optional<TrainCommand> translate(IotThing thing, Command receivedCommand) {
        if(receivedCommand instanceof ThingValueCommand itvC) {
            return Optional.of(new TrainCommand(thing.getControllerId(), thing.getThingId(), itvC.getAttributes(), getLocAddress(thing), getStepMode(thing)));
        } else {
            LOG.error("Could not convert command: {} to train command, incorrect command type", receivedCommand);
            return Optional.empty();
        }
    }

    private int getLocAddress(IotThing thing) {
        return Integer.parseInt(thing.getProperty("locAddress"));
    }

    private StepMode getStepMode(IotThing thing) {
        return StepMode.valueOf(thing.getProperty("dccMode"));
    }
}
