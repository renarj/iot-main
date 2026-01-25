package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.listeners.commands.RebootDriveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RebootCommandListener implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RebootCommandListener.class);

    private final ODriveInterface oDriveInterface;

    public RebootCommandListener(ODriveInterface oDriveInterface) {
        this.oDriveInterface = oDriveInterface;
    }

    @EventSubscribe
    public void receive(RebootDriveCommand driveCommand) {
        LOG.info("Received reboot command for: {}", driveCommand.getNodeId());


    }
}
