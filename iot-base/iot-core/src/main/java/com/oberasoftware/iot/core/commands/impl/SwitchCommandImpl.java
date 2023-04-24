package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.legacymodel.OnOffValue;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class SwitchCommandImpl implements SwitchCommand {

    private final String controllerId;
    private final String thingId;
    private final STATE state;

    public SwitchCommandImpl(String controllerId, String thingId, STATE state) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.state = state;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public STATE getState() {
        return state;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    @Override
    public String toString() {
        return "SwitchCommandImpl{" +
                "state=" + state +
                ", thingId='" + thingId + '\'' +
                '}';
    }

    @Override
    public Value getValue(String property) {
        return getValues().get(property);
    }

    @Override
    public Map<String, Value> getValues() {
        Map<String, Value> valueMap = new HashMap<>();
        valueMap.put(OnOffValue.LABEL, new OnOffValue(state == STATE.ON));

        return valueMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwitchCommandImpl that = (SwitchCommandImpl) o;

        if (!thingId.equals(that.thingId)) return false;
        return state == that.state;

    }

    @Override
    public int hashCode() {
        int result = thingId.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }
}
