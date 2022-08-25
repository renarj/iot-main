package com.oberasoftware.home.agent.service;

import com.oberasoftware.home.api.exceptions.HomeAutomationException;
import com.oberasoftware.home.api.extensions.AutomationExtension;
import com.oberasoftware.home.api.extensions.ExtensionManager;
import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Renze de Vries
 */
@Component
public class AgentExtensionManager implements ExtensionManager {
    private static final Logger LOG = LoggerFactory.getLogger(AgentExtensionManager.class);

    @Autowired(required = false)
    private List<AutomationExtension> extensions;

    @Autowired
    private MQTTTopicEventBus mqttTopicEventBus;

    @Override
    public void activateController(String s) throws HomeAutomationException {
        mqttTopicEventBus.connect();
    }

    @Override
    public void activateExtensions() throws HomeAutomationException {
        extensions.forEach(e -> {
            LOG.info("Activating extension: {}", e);
            e.activate();
        });
    }

    @Override
    public List<AutomationExtension> getExtensions() {
        return extensions;
    }

    @Override
    public Optional<AutomationExtension> getExtension(String extensionId) {
        return extensions.stream().filter(x -> x.getId().equals(extensionId)).findFirst();
    }
}
