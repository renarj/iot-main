package com.oberasoftware.trainautomation;

import com.google.common.collect.Maps;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.train.TrainConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrainAutomationExtension implements AutomationExtension {
    private static final Logger LOG = LoggerFactory.getLogger(TrainAutomationExtension.class);

    public static final String COMMAND_CENTER_TYPE = "commandCenterType";

    private final ThingClient thingClient;

    private final AgentControllerInformation agentControllerInformation;

    private final CommandCenterFactory commandCenterFactory;

    private final TrainCommandHandler commandHandler;

    private final LocThingRepository locRepository;

    public TrainAutomationExtension(ThingClient thingClient, AgentControllerInformation agentControllerInformation, CommandCenterFactory commandCenterFactory, TrainCommandHandler commandHandler, LocThingRepository locRepository) {
        this.thingClient = thingClient;
        this.agentControllerInformation = agentControllerInformation;
        this.commandCenterFactory = commandCenterFactory;
        this.commandHandler = commandHandler;
        this.locRepository = locRepository;
    }

    @Override
    public String getId() {
        return TrainConstants.EXTENSION_ID;
    }

    @Override
    public String getName() {
        return TrainConstants.EXTENSION_NAME;
    }

    @Override
    public Map<String, String> getProperties() {
        return Maps.newHashMap();
    }

    @Override
    public CommandHandler<IotThing> getCommandHandler() {
        return commandHandler;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void activate(IotThing pluginThing) {
        try {
            List<IotThing> commandCenters = thingClient.getThings(agentControllerInformation.getControllerId(),
                    TrainConstants.EXTENSION_ID, "commandstation");

            LOG.info("Activating command centers: {}", commandCenters);
            commandCenters.forEach(this::activateCommandCenter);
        } catch (IOTException e) {
            LOG.error("Could not retrieve command centers", e);
        }
        LOG.info("Finished activating command centers");
    }

    private void activateCommandCenter(IotThing commandCenter) {
        LOG.info("Activating command center connectivity: {}", commandCenter);
        var commandCenterType = commandCenter.getProperties().get(COMMAND_CENTER_TYPE);
        var oCommandCenter = commandCenterFactory.getCommandCenter(commandCenterType);

        oCommandCenter.ifPresentOrElse(c -> {
            LOG.info("Loading command center: {} for {}", commandCenterType, commandCenter);
            c.connect(commandCenter);
        }, () -> {
            LOG.error("Could not find the command center for: {} for thing: {}", commandCenterType, commandCenter);
        });

        LOG.info("Initialization complete, starting loc sync");
        locRepository.startSync();
    }

    @Override
    public void discoverThings(DiscoveryListener listener) {

    }
}
