package com.oberasoftware.home.core.state;


import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface StateDAO {
    State setState(String controllerId, String thingId, String attribute, Value value);
    State getState(String controllerId, String thingId);
    List<State> getStates(String controllerId);
}
