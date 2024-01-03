package com.oberasoftware.iot.core.commands;

import java.util.Map;

/**
 * @author renarj
 */
public interface SwitchCommand extends ItemValueCommand {

    enum STATE {
        ON,
        OFF
    }

    STATE getState(String attribute);

    Map<String, STATE> getStates();
}
