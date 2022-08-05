package com.oberasoftware.home.core.state;


import com.oberasoftware.iot.core.legacymodel.State;
import com.oberasoftware.iot.core.legacymodel.Value;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface StateDAO {
    State setState(String controllerId, String itemId, String label, Value value);

    State getState(String controllerId, String itemId);

    List<State> getStates(String controllerId);
}
