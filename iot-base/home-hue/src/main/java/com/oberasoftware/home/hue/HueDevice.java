package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.Map;

/**
 * @author renarj
 */
public class HueDevice implements IotThing {

    private final String controllerId;
    private final String thingId;
    private final String name;
    private final String parentId;
    private final Map<String, String> properties;

    public HueDevice(String controllerId, String thingId, String name, String parentId, Map<String, String> properties) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.name = name;
        this.parentId = parentId;
        this.properties = properties;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    @Override
    public String getId() {
        return thingId;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getPluginId() {
        return HueExtension.HUE_ID;
    }

    @Override
    public String getFriendlyName() {
        return this.name;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "HueDevice{" +
                "controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", properties=" + properties +
                '}';
    }
}
