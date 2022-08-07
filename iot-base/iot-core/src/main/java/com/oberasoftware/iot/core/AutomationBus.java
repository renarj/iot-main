package com.oberasoftware.iot.core;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.base.event.EventHandler;

/**
 * @author renarj
 */
public interface AutomationBus {
    void publish(Event event);

    void registerHandler(EventHandler handler);

}
