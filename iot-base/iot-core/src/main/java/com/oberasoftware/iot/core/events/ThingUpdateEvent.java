package com.oberasoftware.iot.core.events;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.IotThing;

/**
 * @author renarj
 */
public class ThingUpdateEvent implements Event {

    private final String pluginId;
    private final IotThing thing;

    public ThingUpdateEvent(String pluginId, IotThing thing) {
        this.pluginId = pluginId;
        this.thing = thing;
    }

    public String getPluginId() {
        return pluginId;
    }

    public IotThing getThing() {
        return thing;
    }

    @Override
    public String toString() {
        return "DeviceInformationEvent{" +
                "thing=" + thing +
                ", pluginId='" + pluginId + '\'' +
                '}';
    }
}
