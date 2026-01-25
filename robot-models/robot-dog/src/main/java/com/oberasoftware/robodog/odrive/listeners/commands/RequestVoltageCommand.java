package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RequestVoltageCommand implements Event {
    private final int nodeId;

    public RequestVoltageCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
