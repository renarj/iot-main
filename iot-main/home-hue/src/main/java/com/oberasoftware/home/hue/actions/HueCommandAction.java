package com.oberasoftware.home.hue.actions;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.storage.DeviceItem;
import com.oberasoftware.iot.core.model.storage.GroupItem;

import java.util.List;

/**
 * @author renarj
 */
public interface HueCommandAction<T extends Event> {
    void receive(DeviceItem item, T command);

    void receive(GroupItem groupItem, List<DeviceItem> items, T command);
}
