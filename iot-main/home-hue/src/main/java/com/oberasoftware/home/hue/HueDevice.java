package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.legacymodel.Status;

import java.util.Map;

/**
 * @author renarj
 */
public class HueDevice implements Device {

    private final String id;
    private final String name;
    private final Status status;
    private final Map<String, String> properties;

    public HueDevice(String id, String name, Status status, Map<String, String> properties) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.properties = properties;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "HueDevice{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", properties=" + properties +
                '}';
    }
}
