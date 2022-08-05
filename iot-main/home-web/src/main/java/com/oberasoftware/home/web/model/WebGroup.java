package com.oberasoftware.home.web.model;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import com.oberasoftware.iot.core.model.storage.impl.GroupItemImpl;

import java.util.List;

/**
 * @author Renze de Vries
 */
public class WebGroup extends GroupItemImpl {

    private final List<IotThing> devices;

    public WebGroup(GroupItem group, List<IotThing> devices) {
        super(group.getId(), group.getControllerId(), group.getName(), group.getDeviceIds(), group.getProperties());

        this.devices = devices;
    }

    public List<IotThing> getDevices() {
        return devices;
    }
}
