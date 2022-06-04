package com.oberasoftware.home.core;

import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import com.oberasoftware.jasdb.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ControllerConfiguration {
    @Value("${controller.id:}")
    private String controllerId;

    public String getControllerId() {
        if(StringUtils.stringEmpty(controllerId)) {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new RuntimeHomeAutomationException("Could not determine hostname, cannot start home automation system", e);
            }
        } else {
            return controllerId;
        }
    }
}
