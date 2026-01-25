package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.device.ODriveAttribute;
import com.oberasoftware.robodog.odrive.device.ODriveStateManager;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestVoltageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VoltageListener implements ODriveEventListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(VoltageListener.class);

    private final ODriveInterface oDriveInterface;
    private final ODriveStateManager stateManager;

    public VoltageListener(ODriveStateManager stateManager, ODriveInterface oDriveInterface) {
        this.oDriveInterface = oDriveInterface;
        this.stateManager = stateManager;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.GET_BUS_VOLTAGE;
    }

    @EventSubscribe
    public void receive(RequestVoltageCommand command) {
        this.oDriveInterface.requestBusVoltage(command.getNodeId());
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);
        b.position(0);

        float busVoltage = b.getFloat();
        float busCurrent = b.getFloat();
        LOG.info("Node {} busVoltage={} busCurrent={}", nodeId, busVoltage, busCurrent);

        stateManager.setAttribute(nodeId, ODriveAttribute.BUS_VOLTAGE, busVoltage);
        stateManager.setAttribute(nodeId, ODriveAttribute.BUS_CURRENT, busCurrent);
    }
}
