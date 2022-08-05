package com.oberasoftware.home.service.commands.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.commands.GroupCommand;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.commands.handlers.DeviceCommandHandler;
import com.oberasoftware.iot.core.commands.handlers.GroupCommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.ExtensionManager;
import com.oberasoftware.iot.core.managers.DeviceManager;
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

    @Autowired
    private DeviceManager deviceManager;

    @EventSubscribe
    public void receive(GroupCommand groupCommand) {
        LOG.debug("Received a group command: {}", groupCommand);

        GroupItem groupItem = groupCommand.getGroup();
        List<String> deviceIds = groupItem.getDeviceIds();
        String controllerId = groupItem.getControllerId();

        Map<String, List<IotThing>> pluginDevices = deviceIds.stream()
                .map(d -> deviceManager.findThing(controllerId, d))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(IotThing::getPluginId));

        pluginDevices.forEach((k, v) -> {
            AutomationExtension extension = extensionManager.getExtension(k).get();

            CommandHandler commandHandler = extension.getCommandHandler();

            if(commandHandler instanceof GroupCommandHandler) {
                LOG.debug("CommandHandler: {} supports group commands, sending group command: {}", commandHandler, groupCommand);
                GroupCommandHandler groupCommandHandler = (GroupCommandHandler) commandHandler;

                groupCommandHandler.receive(groupItem, pluginDevices.get(k), groupCommand.getCommand());
            } else if(commandHandler instanceof DeviceCommandHandler) {
                LOG.debug("CommandHandler not able to support group command, sending individual commands: {}", groupCommand);
                DeviceCommandHandler deviceCommandHandler = (DeviceCommandHandler) commandHandler;

                pluginDevices.get(k).forEach(d -> deviceCommandHandler.receive(d, groupCommand.getCommand()));
            }
        });

    }
}
