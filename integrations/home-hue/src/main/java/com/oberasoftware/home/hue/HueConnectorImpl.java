package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.events.ThingUpdateEvent;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.util.IntUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class HueConnectorImpl implements EventHandler, HueConnector {
    private static final Logger LOG = getLogger(HueConnectorImpl.class);

    private static final String APP_NAME = "HomeAutomation";

    private static final String BRIDGE_TOKEN = "bridgeToken";
    private static final String BRIDGE_PORT = "bridgePort";
    private static final String BRIDGE_IP = "BRIDGE_IP";
    private static final String BRIDGE = "bridge-";
    private static final int DEFAULT_PORT = 443;

    private final List<HueBridge> connectedBridges = new ArrayList<>();

    @Autowired
    private AgentControllerInformation controllerInformation;

    @Autowired
    private HueBridgeDiscoveryService hueBridgeDiscoveryService;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private LocalEventBus automationBus;

    @Override
    public void connect(IotThing pluginData) {
        LOG.info("Connecting to Philips HUE bridge");
        startSearchBridge();

        try {
            var hueBridges = agentClient.getChildren(controllerInformation.getControllerId(), pluginData.getThingId());

            LOG.info("We have {} previously configured Hue Bridges", hueBridges.size());
            hueBridges.forEach(b -> {
                var tProperties = b.getProperties();
                String bridgeIp = tProperties.get(BRIDGE_IP);
                int bridgePort = IntUtils.toInt(b.getProperties().get(BRIDGE_PORT), DEFAULT_PORT);
                if(tProperties.containsKey(BRIDGE_IP)) {
                    if(tProperties.containsKey(BRIDGE_TOKEN)) {
                        String bridgeToken = tProperties.get(BRIDGE_TOKEN);

                        var bridge = new HueBridge(b.getThingId(), bridgeIp, bridgePort, bridgeToken);

                        LOG.info("Loading pre-discovered bridge: {}", bridge);
                        automationBus.publish(new HueBridgeDiscovered(bridge));
                    } else {
                        //bridge not authenticated yet
                        var bridge = new HueBridge(b.getThingId(), bridgeIp, bridgePort, null);
                        automationBus.publish(new HueBridgeAuthEvent(bridge));
                    }
                } else {
                    LOG.warn("Hue bridge: {} on controller: {} does not have IP Configured", b.getThingId(), b.getControllerId());
                }
            });
        } catch (IOTException e) {
            throw new RuntimeIOTException("Unable to load Hue Bridges", e);
        }
    }

    @Override
    public List<HueBridge> getBridges() {
        return connectedBridges;
    }

    @Override
    public HueBridge getBridge(String bridgeId) {
        return connectedBridges.stream()
                .filter(b -> b.getBridgeId().equalsIgnoreCase(bridgeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeIOTException("Unable to find bridge"));
    }

    private void startSearchBridge() {
        LOG.info("No existing bridge found, searching for a bridge");
        Set<HueBridge> bridges = hueBridgeDiscoveryService.startBridgeSearch();
        bridges.forEach(b -> {
            LOG.info("Discovered a bridge: {}, authenticating", b);
            Map<String, String> properties = new HashMap<>();
            properties.put(BRIDGE + b.getBridgeId(), b.getBridgeId());

            var pluginThing = new IotThingImpl(controllerInformation.getControllerId(), HueExtension.HUE_ID,
                    HueExtension.HUE_NAME, HueExtension.HUE_ID, null, properties);
            pluginThing.setType("plugin");
            automationBus.publish(new ThingUpdateEvent(HueExtension.HUE_ID, pluginThing));
        });
    }

    @EventSubscribe
    public void receive(HueBridgeDiscovered bridgeEvent) {
        LOG.info("Connected to bridge: {} with token: {}", bridgeEvent.getBridgeIp(), bridgeEvent.getBridgeToken());

        connectedBridges.add(bridgeEvent.getBridge());
    }

    @EventSubscribe
    public void receive(HueBridgeAuthEvent authEvent) throws ExecutionException, InterruptedException {
        LOG.info("Authentication on Hue Bridge required: {}", authEvent);

        HueBridge targetBridge = authEvent.getBridgeDetails();

        LOG.info("Please push the link button on your Philips Hue Bridge: {}", targetBridge.getBridgeIp());
        CompletableFuture<Optional<String>> apiKey = hueBridgeDiscoveryService.getApiKey(targetBridge);

        Optional<String> bridgeToken = apiKey.get();
        if(bridgeToken.isPresent()) {
            var controllerId = controllerInformation.getControllerId();

            var hueBridge = new HueBridge(targetBridge.getBridgeId(), targetBridge.getBridgeIp(), targetBridge.getBridgePort(), bridgeToken.get());
            LOG.info("Successfully authenticated on Hue Bridge: {}", hueBridge);

            var thing = ThingBuilder.create(targetBridge.getBridgeId(), controllerId)
                    .friendlyName("Philips Hue Bridge: " + targetBridge.getBridgeIp())
                    .type("bridge")
                    .parent(HueExtension.HUE_ID)
                    .plugin(HueExtension.HUE_ID)
                    .addProperty(BRIDGE_IP, targetBridge.getBridgeIp())
                    .addProperty(BRIDGE_PORT, Integer.toString(targetBridge.getBridgePort()))
                    .addProperty(BRIDGE_TOKEN, bridgeToken.get())
                    .build();
            automationBus.publish(new ThingUpdateEvent(HueExtension.HUE_ID, thing));

            automationBus.publish(new HueBridgeDiscovered(hueBridge));
        } else {
            LOG.error("Could not authenticate to Hue Bridge: {}", targetBridge);
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
