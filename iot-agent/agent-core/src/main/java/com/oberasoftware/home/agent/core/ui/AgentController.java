package com.oberasoftware.home.agent.core.ui;

import com.oberasoftware.home.agent.core.AgentBootstrap;
import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.iot.core.exceptions.IOTException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class AgentController {
    private static final Logger LOG = getLogger( AgentController.class );

    @Autowired
    private AgentStorage storage;

    @Autowired
    private AgentBootstrap agentBootstrap;

    @RequestMapping("/")
    public String index(Model model) {
        getAndStoreKey(model,"controllerId");
        getAndStoreKey(model, "mqttHost", "mqtt.host");
        getAndStoreKey(model, "mqttPort", "mqtt.port");
        getAndStoreKey(model, "baseUrl", "thing-svc.baseUrl");
        getAndStoreKey(model, "stateBaseUrl", "state-svc.baseUrl");
        getAndStoreKey(model, "apiToken", "thing-svc.apiToken");

        return "index";
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ResponseEntity<Object> postConfiguration(@RequestBody AgentConfig agentConfig) throws IOTException {
        LOG.info("Posting configuration: {}", agentConfig);

        boolean configComplete = agentBootstrap.configure(agentConfig);
        if(configComplete) {
            var reloadResult = agentBootstrap.reload();
            LOG.info("Reloading agent with new configuration: {}", reloadResult);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Configuration incomplete", HttpStatus.BAD_REQUEST);
        }
    }

    private void getAndStoreKey(Model model, String storagekey) {
        getAndStoreKey(model, storagekey, storagekey);
    }

    private void getAndStoreKey(Model model, String modelKey, String storagekey) {
        if(storage.containsValue(storagekey)) {
            model.addAttribute(modelKey, storage.getValue(storagekey));
        }
    }
}
