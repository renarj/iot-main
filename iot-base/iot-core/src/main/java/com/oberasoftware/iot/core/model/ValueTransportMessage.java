package com.oberasoftware.iot.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Renze de Vries
 */
public class ValueTransportMessage {

    @JsonDeserialize(contentAs = ValueImpl.class)
    private Map<String, Value> values;

    private String controllerId;
    private String thingId;

    public ValueTransportMessage(Value value, String controllerId, String thingId, String attribute) {
        this.values = new HashMap<>();
        this.values.put(attribute, value);
        this.controllerId = controllerId;
        this.thingId = thingId;
    }

    public ValueTransportMessage(Map<String, Value> values, String controllerId, String thingId) {
        this.values = values;
        this.controllerId = controllerId;
        this.thingId = thingId;
    }

    public ValueTransportMessage() {
    }


    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public Map<String, Value> getValues() {
        return values;
    }

    public void setValues(Map<String, Value> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "ValueTransportMessage{" +
                "values=" + values +
                ", controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                '}';
    }
}
