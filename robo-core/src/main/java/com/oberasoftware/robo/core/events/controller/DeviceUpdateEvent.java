package com.oberasoftware.robo.core.events.controller;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.IotThing;

/**
 * @author renarj
 */
public class DeviceUpdateEvent implements Event {

    private final String pluginId;
    private final IotThing device;

    public DeviceUpdateEvent(String pluginId, IotThing device) {
        this.pluginId = pluginId;
        this.device = device;
    }

    public String getPluginId() {
        return pluginId;
    }

    public IotThing getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "DeviceInformationEvent{" +
                "device=" + device +
                ", pluginId='" + pluginId + '\'' +
                '}';
    }
}
