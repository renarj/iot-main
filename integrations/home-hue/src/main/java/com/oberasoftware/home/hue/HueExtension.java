package com.oberasoftware.home.hue;

import com.google.common.collect.Maps;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.extensions.ExtensionCapability;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Renze de Vries
 */
@Component
public class HueExtension implements AutomationExtension, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HueExtension.class);

    public static final String HUE_ID = "hue";
    public static final String HUE_NAME = "Philips Hue plugin";

    @Autowired
    private HueConnector hueConnector;

    @Autowired
    private HueCommandHandler hueCommandHandler;

    @Autowired
    private HueDeviceManager hueDeviceManager;

    private DiscoveryListener discoveryListener;

    @Override
    public boolean supports(ExtensionCapability capability) {
        return capability == ExtensionCapability.GroupSupport;
    }

    @Override
    public String getId() {
        return HUE_ID;
    }

    @Override
    public String getName() {
        return HUE_NAME;
    }

    @Override
    public Map<String, String> getProperties() {
        return Maps.newHashMap();
    }

    @Override
    public boolean isReady() {
        return hueConnector.isConnected();
    }

    @Override
    public void discoverThings(DiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    @EventSubscribe
    public void bridgeConnected(HueBridgeDiscovered bridgeDiscovered) {
        LOG.info("Bridge: {} is connected, updating connected devide information", bridgeDiscovered.getBridgeIp());
        String thingId = bridgeDiscovered.getBridge().getBridgeId();

        List<IotThing> things = hueDeviceManager.getDevices(thingId);
        things.forEach(t -> discoveryListener.thingFound(t));
    }

    @Override
    public void activate(IotThing pluginData) {
        hueConnector.connect(pluginData);
    }

    @Override
    public CommandHandler getCommandHandler() {
        return hueCommandHandler;
    }
}
