package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.model.OnOffValue;

public class HueDeviceState {
    private final int brightness;

    private final OnOffValue onOffState;

    private final String lightId;

    private final String bridgeId;

    public HueDeviceState(int brightness, OnOffValue onOffState, String lightId, String bridgeId) {
        this.brightness = brightness;
        this.onOffState = onOffState;
        this.lightId = lightId;
        this.bridgeId = bridgeId;
    }

    public int getBrightness() {
        return brightness;
    }

    public OnOffValue getOnOffState() {
        return onOffState;
    }

    public String getLightId() {
        return lightId;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    @Override
    public String toString() {
        return "HueDeviceState{" +
                "brightness=" + brightness +
                ", onOffState=" + onOffState +
                ", lightId='" + lightId + '\'' +
                ", bridgeId='" + bridgeId + '\'' +
                '}';
    }
}
