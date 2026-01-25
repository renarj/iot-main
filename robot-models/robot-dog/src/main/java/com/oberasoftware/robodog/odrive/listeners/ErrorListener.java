package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

public class ErrorListener implements ODriveEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorListener.class);

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.GET_ERROR;
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        LOG.info("Received error command from node: {}", nodeId);
    }
}
