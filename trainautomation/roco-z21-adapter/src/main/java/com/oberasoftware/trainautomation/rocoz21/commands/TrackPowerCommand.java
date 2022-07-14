package com.oberasoftware.trainautomation.rocoz21.commands;

public class TrackPowerCommand extends Z21Command {

    private static final int COMMAND_OFF = 0x80;
    private static final int COMMAND_ON = 0x81;

    public TrackPowerCommand(OnOffValue desiredState) {
        addHeader(0x40);
        if(desiredState.isOn()) {
            addXHeader(0x21, COMMAND_ON);
        } else {
            addXHeader(0x21, COMMAND_OFF);
        }
    }
}
