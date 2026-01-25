package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RequestPositionCommand implements Event {
    private final int nodeId;

    public RequestPositionCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
