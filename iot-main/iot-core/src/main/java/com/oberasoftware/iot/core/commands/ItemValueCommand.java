package com.oberasoftware.iot.core.commands;

import com.oberasoftware.iot.core.legacymodel.Value;

import java.util.Map;

/**
 * @author renarj
 */
public interface ItemValueCommand extends ItemCommand {

    Value getValue(String property);

    Map<String, Value> getValues();
}
