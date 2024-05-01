package com.oberasoftware.iot.core.commands.handlers;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.model.IotThing;

/**
 * @author renarj
 */
public interface CommandHandler<T extends IotThing> {
    void receive(T thing, Command command);
}
