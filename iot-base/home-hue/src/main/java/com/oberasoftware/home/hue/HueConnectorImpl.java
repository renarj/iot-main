package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.events.ThingUpdateEvent;
import com.oberasoftware.iot.core.events.impl.PluginUpdateEvent;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.util.IntUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    private static final String BRIDGE_IP = "bridgeIp";
    private static final String BRIDGE = "bridge-";
    private static final int DEFAULT_PORT = 443;

    private List<HueBridge> connectedBridges = new ArrayList<>();

    @Autowired
    private AgentControllerInformation agentControllerInformation;

    @Autowired
    private HueBridgeDiscoveryService hueBridgeDiscoveryService;

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private LocalEventBus automationBus;

    @Override
    public void connect(IotThing pluginData) {
        LOG.info("Connecting to Philips HUE bridge");
        if(pluginData.getProperties().isEmpty()) {
            LOG.info("No bridge configured");
            startSearchBridge();
        } else {
            LOG.info("Loading existing bridges");
            Map<String, String> properties = pluginData.getProperties();
            Set<String> bridgeIds = properties.keySet().stream().filter(k -> k.startsWith("bridge-")).map(properties::get).collect(Collectors.toSet());
            if(bridgeIds.isEmpty()) {
                startSearchBridge();
            } else {
                LOG.info("We have {} previously configured Hue Bridges", bridgeIds.size());
                bridgeIds.forEach(b -> {
                    try {
                        var oThing = thingClient.getThing(agentControllerInformation.getControllerId(), BRIDGE + b);
                        oThing.ifPresent(t -> {
                            var tProperties = t.getProperties();
                            String bridgeToken = tProperties.get(BRIDGE_TOKEN);
                            String bridgeIp = tProperties.get(BRIDGE_IP);
                            int bridgePort = IntUtils.toInt(properties.get(BRIDGE_PORT), DEFAULT_PORT);
                            var bridge = new HueBridge(b, bridgeIp, bridgePort, bridgeToken);

                            LOG.info("Loading pre-discovered bridge: {}", bridge);
                            automationBus.publish(new HueBridgeDiscovered(bridge));
                        });
                    } catch (IOTException e) {
                        throw new RuntimeException(e);
                    }

                });
            }
        }
    }

    @Override
    public List<HueBridge> getBridges() {
        return connectedBridges;
    }

    private void startSearchBridge() {
        LOG.info("No existing bridge found, searching for a bridge");
        Set<HueBridge> bridges = hueBridgeDiscoveryService.startBridgeSearch();
        bridges.forEach(b -> {
            LOG.info("Discovered a bridge: {}, authenticating", b);
            automationBus.publish(new HueBridgeAuthEvent(b));
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
            var controllerId = agentControllerInformation.getControllerId();

            Map<String, String> properties = new HashMap<>();
            properties.put(BRIDGE + targetBridge.getBridgeId(), targetBridge.getBridgeId());
            automationBus.publish(new PluginUpdateEvent(controllerId, HueExtension.HUE_ID, HueExtension.HUE_NAME, properties));

            var thingProperties = new HashMap<String, String>();
            thingProperties.put(BRIDGE_IP, targetBridge.getBridgeIp());
            thingProperties.put(BRIDGE_PORT, Integer.toString(targetBridge.getBridgePort()));
            thingProperties.put(BRIDGE_TOKEN, bridgeToken.get());
            var hueBridge = new HueBridge(targetBridge.getBridgeId(), targetBridge.getBridgeIp(), targetBridge.getBridgePort(), bridgeToken.get());
            LOG.info("Successfully authenticated on Hue Bridge: {}", hueBridge);

            var thing = new IotThingImpl();
            thing.setThingId(BRIDGE + targetBridge.getBridgeId());
            thing.setFriendlyName("Philips Hue Bridge: " + targetBridge.getBridgeIp());
            thing.setControllerId(controllerId);
            thing.setParentId(HueExtension.HUE_ID);
            thing.setPluginId(HueExtension.HUE_NAME);
            thing.setProperties(thingProperties);
            automationBus.publish(new ThingUpdateEvent(HueExtension.HUE_ID, thing));

            automationBus.publish(new HueBridgeDiscovered(hueBridge));
        } else {
            LOG.error("Could not authenticate to Hue Bridge: {}", targetBridge);
        }
    }

    @Override
    public boolean isConnected() {
        return !connectedBridges.isEmpty();
    }
}
