package com.oberasoftware.iot.integrations.shelly;

import com.google.common.base.Joiner;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.AgentClient;
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
    private static final String SHELLY_NAME = "SHELLY_NAME";

    @Autowired
    private ShellyCommandHandler commandHandler;

    @Autowired
    private AgentControllerInformation controllerInformation;

    @Autowired
    private ShellyV2ConnectorImpl v2Connector;

    @Autowired
    private ShellyV1ConnectorImpl v1Connector;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private ShellyStatusSync shellyStatusSync;

    @Autowired
    private ShellyRegister shellyRegister;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void activate(IotThing pluginThing) {
        LOG.debug("Activating Shelly extension");

        try {
            var shellyDevices = agentClient.getChildren(controllerInformation.getControllerId(), pluginThing.getThingId());
            shellyDevices.forEach(this::activateShellyDevice);
        } catch (IOTException e) {
            LOG.error("Could not activate existing children for Shelly plugin", e);
        }

        executorService.submit(shellyStatusSync);
    }

    public void activateShellyDevice(IotThing thing) {
        LOG.info("Activating Shelly device: {} on Controller: {}", thing.getThingId(), thing.getControllerId());
        var shellyIp = thing.getProperty(SHELLY_IP);
        try {
            ShellyMetadata.SHELLY_VERSION version = v1Connector.getShellyVersion(shellyIp);

            ShellyMetadata metadata;
            if(version == ShellyMetadata.SHELLY_VERSION.V2) {
                LOG.info("Shelly: {} on controller: {} is a V2 device", thing.getThingId(), thing.getControllerId());
                metadata = activateDevice(v2Connector, thing, shellyIp);
            } else {
                LOG.info("Shelly: {} on controller: {} is a V1 device", thing.getThingId(), thing.getControllerId());
                metadata = activateDevice(v1Connector, thing, shellyIp);
            }
            LOG.info("Retrieved Shelly metadata: {} for Shelly: {} on Controller: {}", metadata, thing.getThingId(), thing.getControllerId());

            if(shellyRegister.findShelly(thing.getControllerId(), thing.getThingId()).isEmpty()) {
                LOG.info("Scheduling shelly: {} for regular syncs", metadata);
                shellyRegister.addShelly(metadata);
            } else {
                LOG.info("Shelly: {} already syncing, skipping config update", shellyIp);
            }
        } catch(IOTException e) {
            LOG.error("Could not activate shelly: " + shellyIp, e);
        } catch(Exception e) {
            LOG.error("", e);
        }
    }

    private ShellyMetadata activateDevice(ShellyConnector connector, IotThing thing, String shellyIp) throws IOTException {
        ShellyMetadata metadata = connector.getShellyInfo(thing.getControllerId(), thing.getThingId(), shellyIp);
        LOG.info("retrieved shelly metadata: {}", metadata);
        if(!thing.getProperties().containsKey(SHELLY_COMPONENTS)) {
            LOG.info("Shelly components not stored, storing for shelly: {} thing: {} on controller: {}",
                    shellyIp, thing.getThingId(), thing.getControllerId());

            agentClient.createOrUpdate(ThingBuilder.create(thing.getThingId(), thing.getControllerId())
                    .plugin(getId()).parent(getId())
                    .type(metadata.getShellyType())
                    .addProperty(SHELLY_NAME, metadata.getShellyName())
                    .addProperty(SHELLY_IP, shellyIp)
                    .addProperty(SHELLY_COMPONENTS, Joiner.on(",").join(metadata.getShellyComponents()))
                    .build());
        }
        return metadata;
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
