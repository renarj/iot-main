package com.oberasoftware.home.agent.core;

import com.oberasoftware.home.agent.core.rules.RuleRunner;
import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.home.agent.core.ui.AgentConfig;
import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.client.ClientBase;
import com.oberasoftware.iot.core.client.StateClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.extensions.ExtensionManager;
import com.oberasoftware.iot.core.util.IntUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AgentBootstrapImpl implements AgentBootstrap {
    private static final Logger LOG = getLogger( AgentBootstrapImpl.class );

    @Autowired
    private AgentClient client;

    @Autowired
    private StateClient stateClient;

    @Autowired
    private AgentStorage storage;

    @Autowired
    private RuleRunner ruleRunner;

    @Autowired
    private MQTTTopicEventBus mqttTopicEventBus;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private AgentControllerInformationImpl agentConfiguration;

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public boolean startAgent() throws IOTException  {
        if(!started.get()) {
            var validation = validateCompleteConfiguration();
            if(validation.isEmpty()) {
                return safelyStartAgent();
            } else {
                LOG.info("Agent configuration incomplete, missing keys: {}", validation);
            }
        }

        return started.get();
    }

    private boolean safelyStartAgent() {
        try {
            String controllerId = agentConfiguration.getControllerId();

            LOG.info("Configuring http and mqtt clients for controller: {}", controllerId);
            configureHttpClient(client, "thing-svc.baseUrl", "thing-svc.apiToken");
            configureHttpClient(stateClient, "state-svc.baseUrl", "thing-svc.apiToken");
            configureMqttConnectivity();

            LOG.info("Activating controller and extensions");
            extensionManager.activateController(controllerId);
            extensionManager.activateExtensions();
            LOG.info("Extensions are activated");

            LOG.info("IoT Agent Started and ready for duty");
            started.set(true);

            var controllerInfo = client.getController(agentConfiguration.getControllerId());
            if(controllerInfo.isPresent()) {
                var properties = controllerInfo.get().getProperties();
                if(!properties.containsKey("rulesEnabled") || properties.containsKey("rulesEnabled") && properties.get("rulesEnabled").equalsIgnoreCase("true")) {
                    LOG.info("Started rules runner");
                    ruleRunner.initializeRules(controllerId);
                } else {
                    LOG.info("Rules runner is disabled for this controller");
                }
            } else {
                LOG.error("Controller does not exist on remote IoT environment");
                System.exit(-1);
            }

        } catch(IOTException e) {
            LOG.error("Could not start the Agent, disabling integrations, please check configuration", e);
            started.set(false);

            mqttTopicEventBus.disconnect();
        }

        return started.get();
    }

    @Override
    public boolean reload() throws IOTException {
        if(started.get()) {
            mqttTopicEventBus.disconnect();
        }
        return safelyStartAgent();
    }

    @Override
    public boolean configure(AgentConfig agentConfig) throws IOTException {
        storage.putValue("thing-svc.baseUrl", agentConfig.getThingService());
        storage.putValue("state-svc.baseUrl", agentConfig.getStateService());
        storage.putValue("thing-svc.apiToken", agentConfig.getApiToken());
        storage.putValue("mqtt.host", agentConfig.getMqttHost());
        storage.putValue("mqtt.port", Integer.toString(agentConfig.getMqttPort()));
        storage.putValue("controllerId", agentConfig.getControllerId());

        return validateCompleteConfiguration().isEmpty();
    }

    private Set<String> validateCompleteConfiguration() {
        Set<String> missingKeys = new HashSet<>();

        checkKey(storage, "thing-svc.baseUrl", missingKeys);
        checkKey(storage, "thing-svc.apiToken", missingKeys);
        checkKey(storage, "state-svc.baseUrl", missingKeys);
        checkKey(storage, "mqtt.host", missingKeys);
        checkKey(storage, "mqtt.port", missingKeys);

        return missingKeys;
    }

    private void checkKey(AgentStorage storage, String key, Set<String> missingKeys) {
        if(!storage.containsValue(key)) {
            missingKeys.add(key);
        }
    }

    private void configureHttpClient(ClientBase clientBase, String svcKey, String tokenKey) {
        var baseUrl = storage.getValue(svcKey);
        var token = storage.getValue(tokenKey);

        LOG.info("Configured Client with baseUrl: {}", baseUrl);
        clientBase.configure(baseUrl, token);
    }

    private void configureMqttConnectivity() {
        var mqttHost = storage.getValue("mqtt.host");
        var mqttPort = IntUtils.toInt(storage.getValue("mqtt.port"), 1883);
        LOG.info("Connecting to MQTT bus on host: {} and port: {}", mqttHost, mqttPort);
        mqttTopicEventBus.initialize(mqttHost, mqttPort, null, null);

        mqttTopicEventBus.connect();
        mqttTopicEventBus.subscribe("/commands/#");
    }
}
