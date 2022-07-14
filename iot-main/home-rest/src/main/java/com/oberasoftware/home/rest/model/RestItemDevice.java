package com.oberasoftware.home.rest.model;

import com.oberasoftware.iot.core.model.State;
import com.oberasoftware.iot.core.model.storage.DeviceItem;

/**
 * @author renarj
 */
public class RestItemDevice {
    private final DeviceItem item;
    private final State state;

    public RestItemDevice(DeviceItem item, State state) {
        this.item = item;
        this.state = state;
    }

    public DeviceItem getItem() {
        return item;
    }

    public State getState() {
        return state;
    }
}
