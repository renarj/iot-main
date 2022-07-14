package com.oberasoftware.home.api.commands.handlers;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.model.storage.Item;

/**
 * @author renarj
 */
public interface CommandHandler<T extends Item> {
    void receive(T item, Command command);
}
