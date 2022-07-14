package com.oberasoftware.trainautomation.rocoz21.commands;

public class CanDetectorsCommand extends Z21Command {
    public CanDetectorsCommand() {
        addHeader(0xC4);
        addSingleByte(0x00);
        addSingleByte(0x00);
        addSingleByte(0xD0);
    }
}
