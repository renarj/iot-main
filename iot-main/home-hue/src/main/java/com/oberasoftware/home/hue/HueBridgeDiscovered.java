package com.oberasoftware.home.hue;


import com.oberasoftware.base.event.Event;

/**
 * @author renarj
 */
public class HueBridgeDiscovered implements Event {
    private final String bridgeIp;
    private final String bridgeToken;

    public HueBridgeDiscovered(String bridgeIp, String bridgeToken) {
        this.bridgeIp = bridgeIp;
        this.bridgeToken = bridgeToken;
    }

    public String getBridgeToken() {
        return bridgeToken;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    @Override
    public String toString() {
        return "HueBridgeDiscovered{" +
                "bridgeIp='" + bridgeIp + '\'' +
                ", bridgeToken='" + bridgeToken + '\'' +
                '}';
    }
}
