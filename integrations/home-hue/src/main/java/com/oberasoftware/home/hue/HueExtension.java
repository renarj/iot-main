package com.oberasoftware.home.hue;

import com.google.common.collect.Maps;
import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.DiscoveryListener;
import com.oberasoftware.iot.core.extensions.ExtensionCapability;
import com.oberasoftware.iot.core.model.IotThing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Renze de Vries
 */
@Component
public class HueExtension implements AutomationExtension {

    public static final String HUE_ID = "hue";
    public static final String HUE_NAME = "Philips Hue plugin";

    @Autowired
    private HueConnector hueConnector;

    @Autowired
    private HueCommandHandler hueCommandHandler;

    @Autowired
    private HueDeviceManager hueDeviceManager;

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
    public void discoverThings(DiscoveryListener listener) {
        hueDeviceManager.getDevices().forEach(listener::thingFound);
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
