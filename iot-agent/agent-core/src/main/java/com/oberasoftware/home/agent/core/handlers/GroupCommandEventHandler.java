package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.commands.GroupCommand;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.commands.handlers.ThingCommandHandler;
import com.oberasoftware.iot.core.commands.handlers.GroupCommandHandler;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.ExtensionManager;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class GroupCommandEventHandler implements EventHandler {
    private static final Logger LOG = getLogger(GroupCommandEventHandler.class);

    @Autowired
    private ExtensionManager extensionManager;

//    @Autowired
//    private DeviceManager deviceManager;
    private ThingClient thingClient;

    @EventSubscribe
    public void receive(GroupCommand groupCommand) {
        LOG.debug("Received a group command: {}", groupCommand);

        GroupItem groupItem = groupCommand.getGroup();
        List<String> deviceIds = groupItem.getDeviceIds();
        String controllerId = groupItem.getControllerId();

        Map<String, List<IotThing>> pluginDevices = deviceIds.stream()
                .map(d -> {
                    try {
                        return thingClient.getThing(controllerId, d);
                    } catch (IOTException e) {
                        throw new RuntimeIOTException("Unable to retrieve group item", e);
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(IotThing::getPluginId));

        pluginDevices.forEach((k, v) -> {
            AutomationExtension extension = extensionManager.getExtensionById(k).get();

            CommandHandler commandHandler = extension.getCommandHandler();

            if(commandHandler instanceof GroupCommandHandler groupCommandHandler) {
                LOG.debug("CommandHandler: {} supports group commands, sending group command: {}", commandHandler, groupCommand);

                groupCommandHandler.receive(groupItem, pluginDevices.get(k), groupCommand.getCommand());
            } else if(commandHandler instanceof ThingCommandHandler thingCommandHandler) {
                LOG.debug("CommandHandler not able to support group command, sending individual commands: {}", groupCommand);

                pluginDevices.get(k).forEach(d -> thingCommandHandler.receive(d, groupCommand.getCommand()));
            }
        });

    }
}
