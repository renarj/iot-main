package com.oberasoftware.home.api.managers;

import com.oberasoftware.iot.core.model.State;
import com.oberasoftware.iot.core.model.Value;

import java.util.Map;

/**
 * @author renarj
 */
public interface StateStore {

    enum SUPPORTED_OPERATIONS {
        WRITE,
        READWRITE
    }

    void store(String itemId, String controllerId, String pluginId, String deviceId, String label, Value value);

    Map<String, State> getStates();

    State getState(String itemId);

    SUPPORTED_OPERATIONS getSupportedOperations();
}
