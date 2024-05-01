package com.oberasoftware.iot.core.commands;

/**
 * @author renarj
 */
public interface ThingCommand extends Command {
    String getControllerId();

    String getThingId();

}
