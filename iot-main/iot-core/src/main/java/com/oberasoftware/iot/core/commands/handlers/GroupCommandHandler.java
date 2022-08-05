package com.oberasoftware.iot.core.commands.handlers;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GroupCommandHandler extends DeviceCommandHandler {
    void receive(GroupItem groupItem, List<IotThing> items, Command command);
}
