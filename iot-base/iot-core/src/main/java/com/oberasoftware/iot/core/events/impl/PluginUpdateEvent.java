package com.oberasoftware.iot.core.events.impl;


import com.oberasoftware.base.event.Event;

import java.util.Map;

/**
 * @author renarj
 */
public class PluginUpdateEvent implements Event {
    private final String controllerId;
    private final String pluginId;
    private final String name;
    private final Map<String, String> properties;

    public PluginUpdateEvent(String controllerId, String pluginId, String name, Map<String, String> properties) {
        this.controllerId = controllerId;
        this.pluginId = pluginId;
        this.name = name;
        this.properties = properties;
    }

    public String getControllerId() {
        return controllerId;
    }

    public String getName() {
        return name;
    }

    public String getPluginId() {
        return pluginId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "PluginUpdateEvent{" +
                "pluginId='" + pluginId + '\'' +
                ", properties='" + properties + '\'' +
                '}';
    }
}
