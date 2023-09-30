package com.oberasoftware.iot.core.events;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.Map;

public interface MultiValueEvent extends Event {
    Map<String, Value> getValues();
}
