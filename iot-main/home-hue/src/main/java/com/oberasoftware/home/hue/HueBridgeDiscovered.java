package com.oberasoftware.home.hue;


import com.oberasoftware.base.event.Event;

/**
 * @author renarj
 */
public class HueBridgeDiscovered implements Event {
    private final HueBridge bridge;

    public HueBridgeDiscovered(HueBridge bridge) {
        this.bridge = bridge;
    }

    public String getBridgeToken() {
        return bridge.getBridgeToken();
    }

    public String getBridgeIp() {
        return bridge.getBridgeIp();
    }

    public HueBridge getBridge() {
        return bridge;
    }

    @Override
    public String toString() {
        return "HueBridgeDiscovered{" +
                "bridge=" + bridge +
                '}';
    }
}
