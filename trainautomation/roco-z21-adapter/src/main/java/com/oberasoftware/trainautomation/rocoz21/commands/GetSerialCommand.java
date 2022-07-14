package com.oberasoftware.trainautomation.rocoz21.commands;

public class GetSerialCommand extends Z21Command {
    public GetSerialCommand() {
        addHeader(0x10);
    }
}
