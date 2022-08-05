package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.legacymodel.State;
import com.oberasoftware.iot.core.legacymodel.Value;

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
