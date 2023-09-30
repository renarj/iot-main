package com.oberasoftware.iot.core.commands;

import com.oberasoftware.iot.core.commands.impl.CommandType;

import java.util.Map;

/**
 * @author renarj
 */
public interface BasicCommand extends ItemCommand {
    CommandType getCommandType();

    Map<String, String> getProperties();

    String getProperty(String property);
}
