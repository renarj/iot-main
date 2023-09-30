package com.oberasoftware.iot.integrations.shelly;

import java.util.List;

public class ShellyMetadata {
    private final String shellyName;
    private final String appName;

    private final String controllerId;

    private final String thingId;

    private final String ip;

    private final List<String> shellyComponents;

    public ShellyMetadata(String controllerId, String thingId, String ip, String shellyName, String appName, List<String> shellyComponents) {
        this.thingId = thingId;
        this.controllerId = controllerId;
        this.ip = ip;
        this.shellyName = shellyName;
        this.appName = appName;
        this.shellyComponents = shellyComponents;
    }

    public String getControllerId() {
        return controllerId;
    }

    public String getThingId() {
        return thingId;
    }

    public String getIp() {
        return ip;
    }

    public String getShellyName() {
        return shellyName;
    }

    public String getAppName() {
        return appName;
    }

    public List<String> getShellyComponents() {
        return shellyComponents;
    }

    @Override
    public String toString() {
        return "ShellyMetadata{" +
                "shellyName='" + shellyName + '\'' +
                ", appName='" + appName + '\'' +
                ", shellyComponents=" + shellyComponents +
                '}';
    }
}
