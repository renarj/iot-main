package com.oberasoftware.iot.integrations.shelly;

import com.google.common.base.Joiner;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class ShellyExtension implements AutomationExtension {
    private static final Logger LOG = getLogger(ShellyExtension.class);
    protected static final String SHELLY_IP = "SHELLY_IP";
    private static final String SHELLY_COMPONENTS = "SHELLY_COMPONENTS";

    @Autowired
    private ShellyCommandHandler commandHandler;

    @Autowired
    private AgentControllerInformation controllerInformation;

    @Autowired
    private ShellyConnector connector;

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private ShellyStatusSync shellyStatusSync;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void activate(IotThing pluginThing) {
        LOG.debug("Activating Shelly extension");

        try {
            var shellyDevices = thingClient.getChildren(controllerInformation.getControllerId(), pluginThing.getThingId());
            shellyDevices.forEach(this::activateShellyDevice);
        } catch (IOTException e) {
            LOG.error("Could not activate existing children for Shelly plugin", e);
        }

        executorService.submit(shellyStatusSync);
    }

    private void activateShellyDevice(IotThing thing) {
        LOG.info("Activating Shelly device: {} on Controller: {}", thing.getThingId(), thing.getControllerId());
        var shellyIp = thing.getProperty(SHELLY_IP);
        try {
            ShellyMetadata metadata = connector.getShellyInfo(thing.getControllerId(), thing.getThingId(), shellyIp);
            LOG.info("retrieved shelly metadata: {}", metadata);
            if(!thing.getProperties().containsKey(SHELLY_COMPONENTS)) {
                LOG.info("Shelly components not stored, storing for shelly: {} thing: {} on controller: {}",
                        shellyIp, thing.getThingId(), thing.getControllerId());

                thingClient.createOrUpdate(ThingBuilder.create(thing.getThingId(), thing.getControllerId())
                        .plugin(getId()).parent(getId())
                        .friendlyName(metadata.getShellyName())
                        .type(metadata.getAppName())
                        .addProperty(SHELLY_IP, shellyIp)
                        .addProperty(SHELLY_COMPONENTS, Joiner.on(",").join(metadata.getShellyComponents()))
                        .build());
            }

            LOG.info("Scheduling shelly: {} for regular syncs", metadata);
            shellyStatusSync.addShelly(metadata);
        } catch(IOTException e) {
            LOG.error("Could not activate shelly: " + shellyIp, e);
        } catch(Exception e) {
            LOG.error("", e);
        }
    }

    @Override
    public void discoverThings(DiscoveryListener listener) {

    }
    @Override
    public String getId() {
        return "Shelly";
    }

    @Override
    public String getName() {
        return "Shelly Plugin";
    }

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    @Override
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public boolean isReady() {
        return true;
    }


}
