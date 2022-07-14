package com.oberasoftware.home.hue;

import java.util.Objects;

final class HueBridge {
    private final String bridgeId;
    private final String bridgeIp;
    private final int bridgePort;

    private final String bridgeToken;

    public HueBridge(String bridgeId, String bridgeIp, int bridgePort) {
        this.bridgeId = bridgeId;
        this.bridgeIp = bridgeIp;
        this.bridgePort = bridgePort;
        this.bridgeToken = "invalid";
    }

    public HueBridge(String bridgeId, String bridgeIp, int bridgePort, String bridgeToken) {
        this.bridgeId = bridgeId;
        this.bridgeIp = bridgeIp;
        this.bridgePort = bridgePort;
        this.bridgeToken = bridgeToken;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    public int getBridgePort() {
        return bridgePort;
    }

    public String getBridgeToken() {
        return bridgeToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HueBridge hueBridge = (HueBridge) o;
        return bridgePort == hueBridge.bridgePort && bridgeId.equals(hueBridge.bridgeId) && bridgeIp.equals(hueBridge.bridgeIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bridgeId, bridgeIp, bridgePort);
    }

    @Override
    public String toString() {
        return "HueBridge{" +
                "bridgeId='" + bridgeId + '\'' +
                ", bridgeIp='" + bridgeIp + '\'' +
                ", bridgePort=" + bridgePort +
                ", bridgeToken='" + bridgeToken + '\'' +
                '}';
    }
}
