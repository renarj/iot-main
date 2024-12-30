package com.oberasoftware.iot.tools;

public class RemoteConfig {
    private final String targetHost;
    private final String targetPort;
    private final String targetLocation;

    public RemoteConfig(String targetHost, String targetPort, String targetLocation) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.targetLocation = targetLocation;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    @Override
    public String toString() {
        return "RemoteConfig{" +
                "targetHost='" + targetHost + '\'' +
                ", targetPort='" + targetPort + '\'' +
                ", targetLocation='" + targetLocation + '\'' +
                '}';
    }
}
