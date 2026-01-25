package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RequestTemperatureCommand implements Event {
    private final int nodeId;

    public RequestTemperatureCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
