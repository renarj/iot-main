package com.oberasoftware.home.rest.model;

import com.oberasoftware.iot.core.legacymodel.State;
import com.oberasoftware.iot.core.model.IotThing;

/**
 * @author renarj
 */
public class RestItemDevice {
    private final IotThing item;
    private final State state;

    public RestItemDevice(IotThing item, State state) {
        this.item = item;
        this.state = state;
    }

    public IotThing getItem() {
        return item;
    }

    public State getState() {
        return state;
    }
}
