package com.oberasoftware.robodog.odrive.listeners.commands;

import com.oberasoftware.base.event.Event;

public class RXCommand implements Event {
    private final int nodeId;
    private final int endpointId;
    private final int value;

    public RXCommand(int nodeId, int endpointId, int value) {
        this.nodeId = nodeId;
        this.endpointId = endpointId;
        this.value = value;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getEndpointId() {
        return endpointId;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RXCommand{" +
                "nodeId=" + nodeId +
                ", endpointId=" + endpointId +
                ", value=" + value +
                '}';
    }
}
