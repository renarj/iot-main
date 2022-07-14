package com.oberasoftware.trainautomation.rocoz21.commands;

public class RailcomGetDataCommand extends Z21Command {
    public RailcomGetDataCommand() {
        addHeader(0x89);
        addSingleByte(0x01);
        add(0x00);
    }
}
