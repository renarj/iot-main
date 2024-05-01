package com.oberasoftware.iot.core.commands;

import com.oberasoftware.iot.core.model.states.Value;

import java.util.Map;

/**
 * @author renarj
 */
public interface ThingValueCommand extends ThingCommand {

    Value getAttribute(String property);

    Map<String, Value> getAttributes();
}
