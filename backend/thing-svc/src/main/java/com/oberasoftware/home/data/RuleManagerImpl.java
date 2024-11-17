package com.oberasoftware.home.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.BlocklyParser;
import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.managers.RuleManager;
import com.oberasoftware.iot.core.model.storage.RuleItem;
import com.oberasoftware.iot.core.model.storage.impl.RuleItemImpl;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import com.oberasoftware.jasdb.core.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private HomeDAO homeDAO;

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private BlocklyParser blocklyParser;

    @Override
    public List<RuleItem> getRules() {
        return homeDAO.findRules();
    }

    @Override
    public List<RuleItem> getRules(String controllerId) {
        return homeDAO.findRules(controllerId);
    }

    @Override
    public RuleItem store(RuleItem ruleItem) throws IOTException {
        RuleItem storeItem = preProcessRule(ruleItem);

        centralDatastore.beginTransaction();
        try {
            RuleItem item = centralDatastore.store(storeItem);
            LOG.debug("Stored rule: {} triggering rule engine", item);

            return item;
        } catch (DataStoreException e) {
            LOG.error("Unable to store rule", e);
            throw new IOTException("Unable to store rule: " + ruleItem);
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
            throw new RuntimeIOTException("Unable to delete rule: " + ruleId);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public Optional<RuleItem> getRule(String ruleId) {
        Optional<RuleItemImpl> ruleItem = homeDAO.findItem(RuleItemImpl.class, ruleId);

        return ruleItem.isPresent() ? Optional.of(ruleItem.get()) : Optional.empty();
    }

    private RuleItem preProcessRule(RuleItem ruleItem) throws BlocklyParseException {
        if(homeDAO.findController(ruleItem.getControllerId()).isPresent()) {
            String blocklyXML = ruleItem.getProperties().get(BLOCKLY_PROPERTY);
            if(StringUtils.stringNotEmpty(blocklyXML)) {
                Rule rule = blocklyParser.toRule(blocklyXML);

                return new RuleItemImpl(ruleItem.getId(), rule.getName(), ruleItem.getControllerId(), ruleItem.getBlocklyData(), ruleItem.getProperties());
            }

            LOG.debug("Could not register blockly xml data, ignoring");
            return ruleItem;

        } else {
            throw new RuntimeIOTException("Unable to process rule, invalid controller specified");
        }
    }


}
