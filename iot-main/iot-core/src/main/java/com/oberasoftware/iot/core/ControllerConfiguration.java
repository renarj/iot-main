package com.oberasoftware.iot.core;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ControllerConfiguration {
    @Value("${controller.id:}")
    private String controllerId;

    public String getControllerId() {
        if(!StringUtils.hasText(controllerId)) {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new RuntimeIOTException("Could not determine hostname, cannot start home automation system", e);
            }
        } else {
            return controllerId;
        }
    }
}
