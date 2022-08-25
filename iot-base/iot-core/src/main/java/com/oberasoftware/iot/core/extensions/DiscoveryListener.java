package com.oberasoftware.iot.core.extensions;

import com.oberasoftware.iot.core.model.IotThing;

public interface DiscoveryListener {
    void thingFound(IotThing thing);
}
