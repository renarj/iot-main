package com.oberasoftware.iot.core.commands;

/**
 * @author renarj
 */
public interface ItemCommand extends Command {
    String getControllerId();

    String getItemId();
}
