package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RebootDriveCommand implements Event {
    private final int nodeId;

    public RebootDriveCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
