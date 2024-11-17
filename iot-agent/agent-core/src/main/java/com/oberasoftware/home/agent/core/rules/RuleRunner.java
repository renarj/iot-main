package com.oberasoftware.home.agent.core.rules;

import com.oberasoftware.home.rules.RuleEngine;
import com.oberasoftware.home.rules.blockly.BlocklyParser;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.storage.HomeDAO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleRunner {
    private static final Logger LOG = LoggerFactory.getLogger(RuleRunner.class);

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private BlocklyParser blocklyParser;

    @Autowired
    private HomeDAO homeDAO;

    @PostConstruct
    public void onStartup() {
        homeDAO.findRules().forEach(r -> {
            try {
                LOG.debug("Registering rule: {}", r);
                var parserRule = blocklyParser.toRule(r.getBlocklyData());
                ruleEngine.register(parserRule);

                LOG.debug("Triggered rule engine start event");
                ruleEngine.onStarted();
            } catch (IOTException e) {
                LOG.error("Could not load rule: " + r, e);
            }
        });
    }

    @PreDestroy
    public void onShutdown() {
        LOG.debug("Triggered rule engine stopping event");
        ruleEngine.onStopping();
    }
}