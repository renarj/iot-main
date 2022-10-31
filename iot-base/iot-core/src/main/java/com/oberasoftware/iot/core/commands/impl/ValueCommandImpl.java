package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class ValueCommandImpl implements ItemValueCommand {

    private final String itemId;
    private final String controllerId;

    private Map<String, Value> values = new HashMap<>();

    public ValueCommandImpl(String controllerId, String itemId, Map<String, Value> values) {
        this.controllerId = controllerId;
        this.itemId = itemId;
        this.values = values;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public Value getValue(String property) {
        return values.get(property);
    }

    @Override
    public Map<String, Value> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueCommandImpl that = (ValueCommandImpl) o;

        if (!itemId.equals(that.itemId)) return false;
        return values.equals(that.values);

    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ValueCommandImpl{" +
                "itemId='" + itemId + '\'' +
                ", values=" + values +
                '}';
    }
}
