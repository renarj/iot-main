package com.oberasoftware.iot.tools;

import com.oberasoftware.iot.core.client.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRunner {
    public static final String URL_FORMAT = "http://%s:%s";

    @Autowired
    protected AgentClient client;

    protected void configure(RemoteConfig config) {
        client.configure(String.format(URL_FORMAT, config.getTargetHost(), config.getTargetPort()), config.getTargetLocation());
    }
}
