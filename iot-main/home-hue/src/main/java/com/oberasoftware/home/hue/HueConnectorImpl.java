package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.AutomationBus;
import com.oberasoftware.iot.core.storage.HomeDAO;
import com.oberasoftware.iot.core.ControllerConfiguration;
import com.oberasoftware.iot.core.util.IntUtils;
import com.oberasoftware.iot.core.events.impl.PluginUpdateEvent;
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

    private static final String BRIDGE_TOKEN = "bridgeToken-";
    private static final String BRIDGE_PORT = "bridgePort-";
    private static final String BRIDGE_IP = "bridgeIp-";
    private static final String BRIDGE = "bridge-";
    private static final int DEFAULT_PORT = 443;

    private String bridgeIp;
    private String bridgeToken;

//    private AtomicBoolean connected = new AtomicBoolean(false);

    private List<HueBridge> connectedBridges = new ArrayList<>();

    @Autowired
    private HueBridgeDiscoveryService hueBridgeDiscoveryService;

    @Autowired
    private AutomationBus automationBus;

    @Autowired
    private ControllerConfiguration controllerConfiguration;

    @Autowired
    private HomeDAO homeDAO;

    @Override
    public void connect() {
        LOG.info("Connecting to Philips HUE bridge");
        if(pluginItem.isEmpty()) {
            LOG.info("No bridge configured");
            startSearchBridge();
        } else {
            LOG.info("Loading existing bridges");
            Map<String, String> properties = pluginItem.get().getProperties();
            Set<String> bridgeIds = properties.keySet().stream().filter(k -> k.startsWith("bridge-")).map(properties::get).collect(Collectors.toSet());
            if(bridgeIds.isEmpty()) {
                startSearchBridge();
            } else {
                LOG.info("We have {} previously configured Hue Bridges", bridgeIds.size());
                bridgeIds.forEach(b -> {
                    String bridgeToken = properties.get(BRIDGE_TOKEN + b);
                    String bridgeIp = properties.get(BRIDGE_IP + b);
                    int bridgePort = IntUtils.toInt(properties.get(BRIDGE_PORT + b), DEFAULT_PORT);
                    var bridge = new HueBridge(b, bridgeIp, bridgePort, bridgeToken);

                    LOG.info("Loading pre-discovered bridge: {}", bridge);
                    automationBus.publish(new HueBridgeDiscovered(bridge));
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
            Map<String, String> properties = new HashMap<>();
            properties.put(BRIDGE + targetBridge.getBridgeId(), targetBridge.getBridgeId());
            properties.put(BRIDGE_IP + targetBridge.getBridgeId(), targetBridge.getBridgeIp());
            properties.put(BRIDGE_PORT + targetBridge.getBridgeId(), Integer.toString(targetBridge.getBridgePort()));
            properties.put(BRIDGE_TOKEN + targetBridge.getBridgeId(), bridgeToken.get());
            var hueBridge = new HueBridge(targetBridge.getBridgeId(), targetBridge.getBridgeIp(), targetBridge.getBridgePort(), bridgeToken.get());

            LOG.info("Successfully authenticated on Hue Bridge: {}", hueBridge);
            automationBus.publish(new PluginUpdateEvent(HueExtension.HUE_ID, HueExtension.HUE_NAME, properties));
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
