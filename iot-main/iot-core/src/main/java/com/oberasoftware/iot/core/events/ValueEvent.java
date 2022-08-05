package com.oberasoftware.iot.core.events;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.legacymodel.Value;

/**
 * @author Renze de Vries
 */
public interface ValueEvent extends Event {
    String getLabel();

    Value getValue();
}
