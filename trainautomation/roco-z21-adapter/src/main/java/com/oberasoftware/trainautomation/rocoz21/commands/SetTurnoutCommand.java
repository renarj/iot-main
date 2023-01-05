package com.oberasoftware.trainautomation.rocoz21.commands;

import com.oberasoftware.iot.core.train.SwitchState;

public class SetTurnoutCommand extends Z21Command {
    public SetTurnoutCommand(int turnout, boolean on, SwitchState state) {
        addHeader(0x40);
        addXHeader(0x53);

        turnout -= 1;
        addSingleByte((turnout &0xff00) >> 8);
        addSingleByte((turnout &0x00ff));

        int command = 0x80;
        if(state == SwitchState.POS_P0) {
            command = command | 0x01;
        }
        if(on) {
            command = command | 0x08;
        }
        //queue the command
        command = command | 0x20;

        addSingleByte(command);
    }
}
