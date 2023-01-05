package com.oberasoftware.trainautomation.rocoz21.commands;

public class GetLocInfoCommand extends Z21Command {
    public GetLocInfoCommand(int address) {
        addHeader(0x40);
        addSingleByte(0xE3);
        addSingleByte(0xF0);
        addSingleByte(getHighAddress(address));
        addSingleByte(getLowAddress(address));
    }

}
