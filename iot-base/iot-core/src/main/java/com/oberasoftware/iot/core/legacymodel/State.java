package com.oberasoftware.iot.core.legacymodel;

import java.util.List;

/**
 * @author renarj
 */
public interface State {
    String getItemId();

    List<StateItem> getStateItems();

    StateItem getStateItem(String label);
}
