package com.oberasoftware.iot.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;

/**
 * @author Renze de Vries
 */
public class ValueTransportMessage {

    @JsonDeserialize(as = ValueImpl.class)
    private Value value;

    private String controllerId;
    private String thingId;
    private String attribute;

    public ValueTransportMessage(Value value, String controllerId, String thingId, String attribute) {
        this.value = value;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attribute = attribute;
    }

    public ValueTransportMessage() {
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
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

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "ValueTransportMessage{" +
                "value=" + value +
                ", controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
