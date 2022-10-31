package com.oberasoftware.iot.core.events;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.states.Value;

/**
 * @author Renze de Vries
 */
public interface ValueEvent extends Event {
    String getAttribute();

    Value getValue();
}
