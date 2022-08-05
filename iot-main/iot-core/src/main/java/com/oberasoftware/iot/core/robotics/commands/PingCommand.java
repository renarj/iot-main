package com.oberasoftware.iot.core.robotics.commands;

import com.oberasoftware.iot.core.commands.Command;

/**
 * @author renarj
 */
public class PingCommand implements Command {
    private final String name;

    public PingCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
