package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class ValueCommandImpl implements ItemValueCommand {

    private final String thingId;
    private final String controllerId;

    private Map<String, Value> values = new HashMap<>();

    public ValueCommandImpl(String controllerId, String thingId, Map<String, Value> values) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.values = values;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getThingId() {
        return thingId;
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

        if (!thingId.equals(that.thingId)) return false;
        return values.equals(that.values);

    }

    @Override
    public int hashCode() {
        int result = thingId.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ValueCommandImpl{" +
                "thingId='" + thingId + '\'' +
                ", values=" + values +
                '}';
    }
}
