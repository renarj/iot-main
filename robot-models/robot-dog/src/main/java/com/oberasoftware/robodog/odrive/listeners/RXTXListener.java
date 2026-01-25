package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.listeners.commands.RXCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RXTXListener implements ODriveEventListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RXTXListener.class);

    private final ODriveInterface oDriveInterface;

    public RXTXListener(ODriveInterface oDriveInterface) {
        this.oDriveInterface = oDriveInterface;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.TX;
    }

    @EventSubscribe
    public void receive(RXCommand command) {
        LOG.info("Received RX command: {}", command);

        oDriveInterface.readParameter(command.getNodeId(), command.getEndpointId());
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        LOG.info("Received TX response from node: {}", nodeId);

        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);
        b.position(0);

        int opCode = b.get();
        int endpointId = b.getShort();
        int ignore = b.get();
        float value = b.getFloat();
        LOG.info("Received value: {} for endpoint: {} on nodeId: {}", value, endpointId, nodeId);
    }
}
