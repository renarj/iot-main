package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.Event;

/**
 * @author renarj
 */
public class HueBridgeAuthEvent implements Event {
    private final HueBridge bridgeDetails;

    public HueBridgeAuthEvent(HueBridge  bridgeDetails) {
        this.bridgeDetails = bridgeDetails;
    }

    public String getBridgeIp() {
        return bridgeDetails.getBridgeIp();
    }

    public HueBridge getBridgeDetails() {
        return bridgeDetails;
    }

    @Override
    public String toString() {
        return "HueBridgeAuthEvent{" +
                "bridgeDetails=" + bridgeDetails +
                '}';
    }
}
