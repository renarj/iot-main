package com.oberasoftware.home.api.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.RuleItem;

import java.util.List;
import java.util.Optional;

/**
 * @author Renze de Vries
 */
public interface RuleManager {
    List<RuleItem> getRules();

    List<RuleItem> getRules(String controllerId);

    RuleItem store(RuleItem ruleItem) throws IOTException;

    void delete(String ruleId);

    Optional<RuleItem> getRule(String ruleId);
}
