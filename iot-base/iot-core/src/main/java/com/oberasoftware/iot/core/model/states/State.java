package com.oberasoftware.iot.core.model.states;

import java.util.List;

/**
 * @author renarj
 */
public interface State {
    String getControllerId();
    String getItemId();
    List<StateItem> getStateItems();

    StateItem getStateItem(String attribute);
}
