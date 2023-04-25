package com.oberasoftware.trainautomation.rocoz21.commandhandlers;

import com.oberasoftware.trainautomation.TrainCommand;

public interface Z21CommandHandler {
    boolean supportsCommand(TrainCommand command);

    void action(TrainCommand command);
}
