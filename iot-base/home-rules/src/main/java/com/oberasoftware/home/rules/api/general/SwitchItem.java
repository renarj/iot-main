package com.oberasoftware.home.rules.api.general;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.oberasoftware.home.rules.api.ItemStatement;
import com.oberasoftware.iot.core.commands.SwitchCommand;

/**
 * @author Renze de Vries
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class SwitchItem implements ItemStatement {

    private String thingId;

    private String controllerId;

    private String attribute;

    private SwitchCommand.STATE state;

    public SwitchItem(String controllerId, String thingId, String attribute, SwitchCommand.STATE state) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attribute = attribute;
        this.state = state;
    }

    public SwitchItem() {
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public SwitchCommand.STATE getState() {
        return state;
    }

    public void setState(SwitchCommand.STATE state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SwitchItem{" +
                "thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", attribute='" + attribute + '\'' +
                ", state=" + state +
                '}';
    }
}
