package com.oberasoftware.iot.core.train.model;

public class Locomotive {
    private int locAddress;

    private String controllerId;

    private String thingId;

    private String name;

    public Locomotive(int locAddress, String controllerId, String thingId, String name) {
        this.locAddress = locAddress;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.name = name;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public int getLocAddress() {
        return locAddress;
    }

    public void setLocAddress(int locAddress) {
        this.locAddress = locAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Locomotive{" +
                "locAddress=" + locAddress +
                ", name='" + name + '\'' +
                '}';
    }
}
