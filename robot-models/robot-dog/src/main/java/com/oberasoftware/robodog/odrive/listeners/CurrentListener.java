package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.device.ODriveAttribute;
import com.oberasoftware.robodog.odrive.device.ODriveStateManager;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestCurrentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CurrentListener implements ODriveEventListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentListener.class);

    private final ODriveStateManager stateManager;
    private final ODriveInterface oDriveInterface;

    public CurrentListener(ODriveStateManager stateManager, ODriveInterface oDriveInterface) {
        this.stateManager = stateManager;
        this.oDriveInterface = oDriveInterface;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.GET_IQ;
    }

    @EventSubscribe
    public void eventReceive(RequestCurrentCommand command) {
        LOG.info("Request for current on node: {}", command.getNodeId());
        oDriveInterface.requestCurrentEstimate(command.getNodeId());
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);
        b.position(0);

        float iqSet = b.getFloat();
        float iqMeasure = b.getFloat();
        LOG.info("Node {} iqSet={} iqMeasure={}", nodeId, iqSet, iqMeasure);

        stateManager.setAttribute(nodeId, ODriveAttribute.CURRENT_TARGET, iqSet);
        stateManager.setAttribute(nodeId, ODriveAttribute.CURRENT, iqMeasure);
    }
}
