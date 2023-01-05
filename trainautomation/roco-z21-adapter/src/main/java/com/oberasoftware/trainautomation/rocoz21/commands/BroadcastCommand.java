package com.oberasoftware.trainautomation.rocoz21.commands;

public enum BroadcastCommand {


    INFO_MESSAGES(0x00000001),

    LOCO_INFO_SUBSCRIBE_MODIFIED(0x00010000),
    LAN_CAN_MESSAGES(0x00080000),

    LOCONET_MESSAGES(0x08000000);

    private final int flag;

    BroadcastCommand(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
