package com.oberasoftware.iot.core.commands;

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
