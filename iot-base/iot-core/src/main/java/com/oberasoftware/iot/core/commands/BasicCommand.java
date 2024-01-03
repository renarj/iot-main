package com.oberasoftware.iot.core.commands;

import com.oberasoftware.iot.core.commands.impl.CommandType;

import java.util.Map;

/**
 * @author renarj
 */
public interface BasicCommand extends ItemCommand {
    CommandType getCommandType();

    String getAttribute(String attribute);

    Map<String, String> getAttributes();
}
