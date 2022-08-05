package com.oberasoftware.iot.core.events;


import com.oberasoftware.iot.core.legacymodel.State;

/**
 * @author renarj
 */
public class StateUpdateEvent implements ItemEvent {
    private final State state;
    private final String itemId;

    public StateUpdateEvent(State state) {
        this.state = state;
        this.itemId = state.getItemId();
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    public State getState() {
        return state;
    }
}
