package com.oberasoftware.home.agent.core.ui;

public class AgentConfig {
    private String mqttHost;
    private Integer mqttPort;
    private String thingService;
    private String apiToken;
    private String controllerId;

    public String getMqttHost() {
        return mqttHost;
    }

    public void setMqttHost(String mqttHost) {
        this.mqttHost = mqttHost;
    }

    public Integer getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(Integer mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getThingService() {
        return thingService;
    }

    public void setThingService(String thingService) {
        this.thingService = thingService;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public String toString() {
        return "AgentConfig{" +
                "mqttHost='" + mqttHost + '\'' +
                ", mqttPort=" + mqttPort +
                ", thingService='" + thingService + '\'' +
                ", apiToken='" + apiToken + '\'' +
                ", controllerId='" + controllerId + '\'' +
                '}';
    }
}
