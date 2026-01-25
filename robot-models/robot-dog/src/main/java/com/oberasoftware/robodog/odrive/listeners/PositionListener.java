package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.device.ODriveAttribute;
import com.oberasoftware.robodog.odrive.device.ODriveStateManager;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestPositionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PositionListener implements ODriveEventListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PositionListener.class);

    private final ODriveInterface oDriveInterface;
    private final ODriveStateManager stateManager;

    public PositionListener(ODriveStateManager stateManager, ODriveInterface oDriveInterface) {
        this.oDriveInterface = oDriveInterface;
        this.stateManager = stateManager;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.GET_ENCODER_ESTIMATES;
    }

    @EventSubscribe
    public void receive(RequestPositionCommand command) {
        LOG.info("Request for position on node: {}", command.getNodeId());

        oDriveInterface.requestEncoderEstimates(command.getNodeId());
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        LOG.info("Received encoder command from node: {}", nodeId);
        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);
        b.position(0);

        float pos = b.getFloat();
        float vel = b.getFloat();

        LOG.info("Node {} → Encoder: pos={} turns, vel={} turns/s", nodeId, pos, vel);
        this.stateManager.setAttribute(nodeId, ODriveAttribute.POSITION, pos);
        this.stateManager.setAttribute(nodeId, ODriveAttribute.VELOCITY, vel);
    }
}
