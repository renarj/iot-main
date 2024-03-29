package com.oberasoftware.iot.core.events.impl;

import com.oberasoftware.iot.core.events.ItemValueEvent;
import com.oberasoftware.iot.core.model.states.Value;

/**
 * @author Renze de Vries
 */
public class ItemNumericValue implements ItemValueEvent {
    private final String itemId;
    private final Value value;
    private final String label;

    public ItemNumericValue(String itemId, Value value, String label) {
        this.itemId = itemId;
        this.value = value;
        this.label = label;
    }

    @Override
    public String getItemId() {
        return this.itemId;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public String getAttribute() {
        return label;
    }
}
