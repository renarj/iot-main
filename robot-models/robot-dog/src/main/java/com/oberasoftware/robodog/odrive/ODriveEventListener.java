package com.oberasoftware.robodog.odrive;

import tel.schich.javacan.CanFrame;

public interface ODriveEventListener {

    ODriveCommands getCommandId();

    void onEvent(int nodeId, int cmdId, CanFrame frame);
}
