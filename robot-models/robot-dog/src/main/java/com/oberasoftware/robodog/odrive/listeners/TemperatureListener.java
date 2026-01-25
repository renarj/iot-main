package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.device.ODriveAttribute;
import com.oberasoftware.robodog.odrive.device.ODriveStateManager;
import com.oberasoftware.robodog.odrive.listeners.commands.RequestTemperatureCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TemperatureListener implements ODriveEventListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TemperatureListener.class);

    private final ODriveInterface oDriveInterface;
    private final ODriveStateManager stateManager;

    public TemperatureListener(ODriveStateManager stateManager, ODriveInterface oDriveInterface) {
        this.oDriveInterface = oDriveInterface;
        this.stateManager = stateManager;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.GET_TEMPERATURE;
    }

    @EventSubscribe
    public void receive(RequestTemperatureCommand command) {
        oDriveInterface.requestTemperature(command.getNodeId());
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);
        b.position(0);

        float boardTemperature = b.getFloat();
        float motorTemperature = b.getFloat();

        LOG.info("Node {} boardTemperature={} motorTemperature={}", nodeId, boardTemperature, motorTemperature);
        stateManager.setAttribute(nodeId, ODriveAttribute.BOARD_TEMPERATURE, boardTemperature);
        stateManager.setAttribute(nodeId, ODriveAttribute.MOTOR_TEMPERATURE, motorTemperature);

    }
}
