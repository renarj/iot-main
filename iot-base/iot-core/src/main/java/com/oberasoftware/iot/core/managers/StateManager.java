package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.legacymodel.State;
import com.oberasoftware.iot.core.legacymodel.Status;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.Map;

/**
 * @author renarj
 */
public interface StateManager {
    State updateItemState(String itemId, String label, Value value);

    State updateDeviceState(IotThing item, String label, Value value);

    State updateStatus(IotThing item, Status newStatus);

    Map<String, State> getStates();

    State getState(String itemId);
}
