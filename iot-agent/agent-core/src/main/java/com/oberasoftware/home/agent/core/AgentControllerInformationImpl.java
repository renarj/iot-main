package com.oberasoftware.home.agent.core;

import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.iot.core.AgentControllerInformation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AgentControllerInformationImpl implements AgentControllerInformation {
    private static final Logger LOG = getLogger( AgentControllerInformationImpl.class );

    @Value("${controller.id:}")
    private String controllerId;

    @Autowired
    private AgentStorage agentStorage;

    @Override
    public String getControllerId() {
        var storedControllerId = agentStorage.getValue("controllerId");
        if(storedControllerId != null) {
            LOG.debug("Controller Id: {} is found in stored configuration", storedControllerId);
            return storedControllerId;
        } else {
            if(!StringUtils.hasText(controllerId)) {
                LOG.warn("No controllerId has been configured, please configure your Agent first");
                return null;
            } else {
                LOG.debug("ControllerId: {} was statically configured, storing in local configuration store", controllerId);
                agentStorage.putValue("controllerId", controllerId);
                return controllerId;
            }
        }
    }
}
