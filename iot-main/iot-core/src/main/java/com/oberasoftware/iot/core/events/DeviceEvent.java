package com.oberasoftware.iot.core.events;

import com.oberasoftware.base.event.Event;

/**
 * @author renarj
 */
public interface DeviceEvent extends Event {
    String getControllerId();

    String getDeviceId();
}
