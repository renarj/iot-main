package com.oberasoftware.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.home.api.managers.RuleManager;
import com.oberasoftware.iot.core.model.storage.RuleItem;
import com.oberasoftware.home.api.storage.CentralDatastore;
import com.oberasoftware.home.api.storage.HomeDAO;
import com.oberasoftware.home.core.model.storage.RuleItemImpl;
import com.oberasoftware.home.rules.RuleEngine;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.BlocklyParser;
import com.oberasoftware.jasdb.core.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class RuleManagerImpl implements RuleManager {
    private static final Logger LOG = getLogger(RuleManagerImpl.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String BLOCKLY_PROPERTY = "Blockly";

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private BlocklyParser blocklyParser;

    @PostConstruct
    public void onStartup() {
        homeDAO.findRules().forEach(r -> {
            try {
                LOG.debug("Registering rule: {}", r);
                ruleEngine.register(toRule(r));

                LOG.debug("Triggered rule engine start event");
                ruleEngine.onStarted();
            } catch (HomeAutomationException e) {
                LOG.error("Could not load rule: " + r.toString(), e);
            }
        });
    }

    @PreDestroy
    public void onShutdown() {
        LOG.debug("Triggered rule engine stopping event");
        ruleEngine.onStopping();
    }

    @Override
    public List<RuleItem> getRules() {
        return homeDAO.findRules();
    }

    @Override
    public List<RuleItem> getRules(String controllerId) {
        return homeDAO.findRules(controllerId);
    }

    @Override
    public RuleItem store(RuleItem ruleItem) throws HomeAutomationException {
        RuleItem storeItem = preProcessRule(ruleItem);

        centralDatastore.beginTransaction();
        try {
            RuleItem item = centralDatastore.store(storeItem);
            LOG.debug("Stored rule: {} triggering rule engine", item);
            ruleEngine.register(toRule(item));

            return item;
        } catch (DataStoreException e) {
            LOG.error("Unable to store rule", e);
            throw new HomeAutomationException("Unable to store rule: " + ruleItem);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public void delete(String ruleId) {
        centralDatastore.beginTransaction();
        try {
            centralDatastore.delete(RuleItemImpl.class, ruleId);
        } catch (DataStoreException e) {
            throw new RuntimeHomeAutomationException("Unable to delete rule: " + ruleId);
        } finally {
            centralDatastore.commitTransaction();

            ruleEngine.removeRule(ruleId);
        }
    }

    @Override
    public Optional<RuleItem> getRule(String ruleId) {
        Optional<RuleItemImpl> ruleItem = homeDAO.findItem(RuleItemImpl.class, ruleId);

        return ruleItem.isPresent() ? Optional.of(ruleItem.get()) : Optional.empty();
    }

    private RuleItem preProcessRule(RuleItem ruleItem) throws BlocklyParseException {
        String blocklyXML = ruleItem.getProperties().get(BLOCKLY_PROPERTY);
        if(StringUtils.stringNotEmpty(blocklyXML)) {
            Rule rule = blocklyParser.toRule(blocklyXML);
            String json = toJson(rule);

            return new RuleItemImpl(ruleItem.getId(), rule.getName(), ruleItem.getControllerId(), json, ruleItem.getProperties());
        }

        LOG.debug("Could not register blockly xml data, ignoring");
        return ruleItem;
    }

    private String toJson(Rule rule) {
        StringWriter writer = new StringWriter();
        try {
            OBJECT_MAPPER.writeValue(writer, rule);

            return writer.toString();
        } catch (IOException e) {
            LOG.error("Could not serialize rule to json", e);
            return null;
        }
    }

    private Rule toRule(RuleItem item) {
        try {
            Rule rule = OBJECT_MAPPER.readValue(item.getRule(), Rule.class);
            rule.setId(item.getId());

            return rule;
        } catch (IOException e) {
            LOG.error("Could not parse Rule: " + item.getRule());
            return null;
        }
    }
}
