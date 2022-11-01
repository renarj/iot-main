package com.oberasoftware.home.rules.test;

import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Renze de Vries
 */
@Component
public class MockStateManager implements StateManager {

    private Map<String, State> stateMap = new HashMap<>();

    @Override
    public State updateItemState(String controllerId, String thingId, String attribute, Value value) {
        return null;
    }

    @Override
    public State updateItemState(IotThing item, String attribute, Value value) {
        return null;
    }

    @Override
    public Map<String, State> getStates(String controllerId) {
        return null;
    }

    @Override
    public State getState(String controllerId, String itemId) {
        return stateMap.get(itemId);
    }

    public void addState(State state) {
        this.stateMap.put(state.getItemId(), state);
    }
}
