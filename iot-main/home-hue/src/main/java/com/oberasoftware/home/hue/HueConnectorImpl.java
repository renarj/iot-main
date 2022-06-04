package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.api.AutomationBus;
import com.oberasoftware.home.api.events.controller.PluginUpdateEvent;
import com.oberasoftware.home.api.model.storage.PluginItem;
import io.github.zeroone3010.yahueapi.Hue;
import io.github.zeroone3010.yahueapi.HueBridge;
import io.github.zeroone3010.yahueapi.discovery.HueBridgeDiscoveryService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class HueConnectorImpl implements EventHandler, HueConnector {
    private static final Logger LOG = getLogger(HueConnectorImpl.class);

    private static final String APP_NAME = "HomeAutomation";

    private String bridgeIp;
    private String bridgeToken;

    private AtomicBoolean connected = new AtomicBoolean(false);

    private List<Hue> connectedBridges = new ArrayList<>();

    @Autowired
    private AutomationBus automationBus;

    @Override
    public void connect(Optional<PluginItem> pluginItem) {
        LOG.info("Connecting to Philips HUE bridge");


        if(!pluginItem.isPresent()) {
            LOG.info("No bridge configured");
            startSearchBrige();
        } else {
            Map<String, String> properties = pluginItem.get().getProperties();
            this.bridgeIp = properties.get("bridgeIp");
            this.bridgeToken = properties.get("bridgeToken");
            if(bridgeIp != null && bridgeToken != null) {
                LOG.info("Existing bridge found: {} token: {}", bridgeIp, bridgeToken);
                automationBus.publish(new HueBridgeDiscovered(bridgeIp, bridgeToken));
            } else {
                startSearchBrige();
            }

        }
    }

    @Override
    public List<Hue> getBridges() {
        return connectedBridges;
    }

    private void startSearchBrige() {
        LOG.info("No existing bridge found, searching for a bridge");
        HueListener hueListener = new HueListener();
        new HueBridgeDiscoveryService().discoverBridges(hueListener);
    }

//    @Override
//    public PHHueSDK getSdk() {
//        return sdk;
//    }
//
//    @Override
//    public PHBridge getBridge() {
//        return sdk.getSelectedBridge();
//    }

    private class HueListener implements Consumer<HueBridge> {
        @Override
        public void accept(HueBridge hueBridge) {
            hueBridge.getIp();


        }
//
//        @Override
//        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
//            LOG.debug("Cache updated");
//        }
//
//        @Override
//        public void onBridgeConnected(PHBridge phBridge, String s) {
//            LOG.info("Bridge connected: {} with user: {}", phBridge, bridgeUser);
//            connected.set(true);
//
//            sdk.setSelectedBridge(phBridge);
//            sdk.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
//
//            Map<String, String> properties = new HashMap<>();
//            properties.put("bridgeIp", bridgeIp);
//            properties.put("username", bridgeUser);
//
//            automationBus.publish(new PluginUpdateEvent(HueExtension.HUE_ID, HueExtension.HUE_NAME, properties));
//        }
//
//        @Override
//        public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
//            LOG.info("Hue authentication required: {}", phAccessPoint.getIpAddress());
//            automationBus.publish(new HueBridgeAuthEvent(phAccessPoint));
//        }
//
//        @Override
//        public void onAccessPointsFound(List<PHAccessPoint> list) {
//            list.forEach(a -> LOG.debug("Found accesspoint: {}", a.getIpAddress()));
//
//            if(list.size() == 1) {
//                Optional<PHAccessPoint> ap = list.stream().findFirst();
//
//                bridgeUser = UUID.randomUUID().toString();
//                bridgeIp = ap.get().getIpAddress();
//
//                ap.ifPresent(a -> automationBus.publish(new HueBridgeDiscovered(a.getIpAddress(), bridgeUser)));
//            } else {
//                LOG.warn("Detected multiple accesspoints");
//            }
//        }
//
//        @Override
//        public void onError(int i, String s) {
//            LOG.error("Hue Connection error: {} reason: {}", i, s);
//        }
//
//        @Override
//        public void onConnectionResumed(PHBridge phBridge) {
//            LOG.trace("Connection resumed");
//        }
//
//        @Override
//        public void onConnectionLost(PHAccessPoint phAccessPoint) {
//            LOG.debug("Connection lost");
//        }
//
//        @Override
//        public void onParsingErrors(List<PHHueParsingError> list) {
//            LOG.debug("Parsing error");
//        }
    }

    @EventSubscribe
    public void receive(HueBridgeDiscovered bridgeEvent) {
        LOG.info("Connecting to bridge: {} with token: {}", bridgeEvent.getBridgeIp(), bridgeEvent.getBridgeToken());

        connectedBridges.add(new Hue(bridgeEvent.getBridgeIp(), bridgeEvent.getBridgeToken()));
    }

    @EventSubscribe
    public void receive(HueBridgeAuthEvent authEvent) throws ExecutionException, InterruptedException {
        LOG.info("Authentication on bridge required: {}", authEvent);

        final CompletableFuture<String> apiKey = Hue.hueBridgeConnectionBuilder(bridgeIp).initializeApiConnection(APP_NAME);
        LOG.info("Please push the link button on your Philips Hue Bridge: {}", authEvent.getBridgeIp());

        final String bridgeToken = apiKey.get();
        Map<String, String> properties = new HashMap<>();
        properties.put("bridgeIp", bridgeIp);
        properties.put("bridgeToken", bridgeToken);
        automationBus.publish(new PluginUpdateEvent(HueExtension.HUE_ID, HueExtension.HUE_NAME, properties));
        automationBus.publish(new HueBridgeDiscovered(bridgeIp, bridgeToken));
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }
}
