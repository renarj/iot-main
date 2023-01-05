package com.oberasoftware.trainautomation.rocoz21.commands;

public class GetTurnoutCommand extends Z21Command {
    public GetTurnoutCommand(int address) {
        addHeader(0x40);
        addSingleByte(0x43);
        addSingleByte((address & 0xff00)>>8);
        addSingleByte(address & 0x00ff);
    }
}
