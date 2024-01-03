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

    private final Map<String, STATE> attributeStates;

    public SwitchCommandImpl(String controllerId, String thingId, Map<String, STATE> attributeStates) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attributeStates = attributeStates;
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
    public STATE getState(String attribute) {
        return attributeStates.get(attribute);
    }

    @Override
    public Map<String, STATE> getStates() {
        return attributeStates;
    }

    @Override
    public String toString() {
        return "SwitchCommandImpl{" +
                "attributeStates=" + attributeStates +
                ", thingId='" + thingId + '\'' +
                '}';
    }

    @Override
    public Value getAttribute(String property) {
        return convert(attributeStates.get(property));
    }

    @Override
    public Map<String, Value> getAttributes() {
        Map<String, Value> valueMap = new HashMap<>();
        attributeStates.forEach((k, v) -> valueMap.put(k, convert(v)));
        return valueMap;
    }

    private OnOffValue convert(STATE state) {
        return new OnOffValue(state == STATE.ON);
    }
}
