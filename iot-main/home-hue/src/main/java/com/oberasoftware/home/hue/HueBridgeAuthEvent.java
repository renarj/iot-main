package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.Event;

/**
 * @author renarj
 */
public class HueBridgeAuthEvent implements Event {
    private final String bridgeIp;

    public HueBridgeAuthEvent(String bridgeIp) {
        this.bridgeIp = bridgeIp;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    @Override
    public String toString() {
        return "HueBridgeAuthEvent{" +
                "bridgeIp='" + bridgeIp + '\'' +
                '}';
    }
}
