package com.oberasoftware.home.api.commands;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.iot.core.model.storage.GroupItem;

/**
 * @author Renze de Vries
 */
public interface GroupCommand extends Event {
    ItemCommand getCommand();

    GroupItem getGroup();
}
