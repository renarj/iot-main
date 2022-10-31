package com.oberasoftware.home.hue.actions;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;

import java.util.List;

/**
 * @author renarj
 */
public interface HueCommandAction<T extends Event> {
    void receive(IotThing item, T command);

    void receive(GroupItem groupItem, List<IotThing> items, T command);
}
