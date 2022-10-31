package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.Map;

/**
 * @author renarj
 */
public interface StateStore {

    enum SUPPORTED_OPERATIONS {
        WRITE,
        READWRITE
    }

    void store(String controllerId, String thingId, String attribute, Value value);

    Map<String, State> getStates();

    State getState(String controllerId, String thingId);

    SUPPORTED_OPERATIONS getSupportedOperations();
}
