package com.oberasoftware.iot.integrations.shelly;

import java.util.List;

public class ShellyMetadata {
    public enum SHELLY_VERSION {
        V1,
        V2
    }

    private final String shellyName;
    private final String shellyType;

    private final String controllerId;

    private final String thingId;

    private final String ip;

    private final SHELLY_VERSION version;

    private final List<String> shellyComponents;

    public ShellyMetadata(String controllerId, String thingId, String ip, SHELLY_VERSION version, String shellyName, String shellyType, List<String> shellyComponents) {
        this.thingId = thingId;
        this.controllerId = controllerId;
        this.ip = ip;
        this.version = version;
        this.shellyName = shellyName;
        this.shellyType = shellyType;
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

    public String getShellyType() {
        return shellyType;
    }

    public List<String> getShellyComponents() {
        return shellyComponents;
    }

    public SHELLY_VERSION getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ShellyMetadata{" +
                "shellyName='" + shellyName + '\'' +
                ", appName='" + shellyType + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", ip='" + ip + '\'' +
                ", version=" + version +
                ", shellyComponents=" + shellyComponents +
                '}';
    }
}
