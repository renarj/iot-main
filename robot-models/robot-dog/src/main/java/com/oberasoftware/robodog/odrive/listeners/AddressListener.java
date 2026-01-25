package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import com.oberasoftware.robodog.odrive.device.ODriveStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

public class AddressListener implements ODriveEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(AddressListener.class);

    private final ODriveStateManager stateManager;

    public AddressListener() {
        this(null);
    }

    public AddressListener(ODriveStateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.ADDRESS;
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        LOG.info("Received address command from node: {}", nodeId);
        if (stateManager != null) {
            stateManager.registerNode(nodeId);
        }
    }
}
