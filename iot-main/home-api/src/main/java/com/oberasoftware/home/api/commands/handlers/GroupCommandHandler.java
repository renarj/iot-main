package com.oberasoftware.home.api.commands.handlers;

import com.oberasoftware.iot.core.commands.Command;
import com.oberasoftware.iot.core.model.storage.DeviceItem;
import com.oberasoftware.iot.core.model.storage.GroupItem;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GroupCommandHandler extends DeviceCommandHandler {
    void receive(GroupItem groupItem, List<DeviceItem> items, Command command);
}
