package com.oberasoftware.iot.core.events;


import com.oberasoftware.iot.core.model.states.State;

/**
 * @author renarj
 */
public class StateUpdateEvent implements ItemEvent {
    private final State state;
    private final String itemId;

    private final String attribute;

    public StateUpdateEvent(State state, String attribute) {
        this.state = state;
        this.itemId = state.getItemId();
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
    @Override
    public String getItemId() {
        return itemId;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "StateUpdateEvent{" +
                "state=" + state +
                ", itemId='" + itemId + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
