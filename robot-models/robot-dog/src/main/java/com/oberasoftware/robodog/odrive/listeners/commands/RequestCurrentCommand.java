package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RequestCurrentCommand implements Event {
    private final int nodeId;

    public RequestCurrentCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
