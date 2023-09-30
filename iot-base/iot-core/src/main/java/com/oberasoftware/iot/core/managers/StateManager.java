package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.Map;
import java.util.Optional;

/**
 * @author renarj
 */
public interface StateManager {
    State updateItemState(String controllerId, String thingId, String attribute, Value value);
    State updateItemState(IotThing item, String attribute, Value value);
    Map<String, State> getStates(String controllerId);
    Optional<State> getState(String controllerId, String itemId);
}
