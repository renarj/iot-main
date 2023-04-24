package com.oberasoftware.trainautomation.rocoz21.commandhandlers;

import com.oberasoftware.iot.core.commands.ItemValueCommand;

public interface Z21CommandHandler {
    boolean supportsCommand(ItemValueCommand command);

    void action(ItemValueCommand command);
}
