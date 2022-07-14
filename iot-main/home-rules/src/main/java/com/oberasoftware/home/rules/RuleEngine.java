package com.oberasoftware.home.rules;

import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.iot.core.exceptions.IOTException;

/**
 * @author Renze de Vries
 */
public interface RuleEngine {
    void register(Rule rule) throws IOTException;

    void evalRule(String id);

    void removeRule(String id);

    void onStarted();

    void onStopping();
}
