package com.oberasoftware.home.api.commands;

import com.oberasoftware.iot.core.commands.ItemValueCommand;

/**
 * @author renarj
 */
public interface SwitchCommand extends ItemValueCommand {
    enum STATE {
        ON,
        OFF
    }

    STATE getState();
}
