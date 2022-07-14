package com.oberasoftware.trainautomation.rocoz21.commands;

public class GetLanStatus extends Z21Command {
    public GetLanStatus() {
        addHeader(0x40);
        addXHeader(0x21, 0x24);
    }
}
