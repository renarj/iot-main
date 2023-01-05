package com.oberasoftware.iot.core.train.model;

import com.oberasoftware.iot.core.train.SwitchState;

import java.util.Map;

public class Turnout {
    private String controllerId;
    private String thingId;

    private int turnoutId;

    private Map<String, SwitchState> states;

    public Turnout(String controllerId, String thingId, int turnoutId) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.turnoutId = turnoutId;
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

    public int getTurnoutId() {
        return turnoutId;
    }

    public void setTurnoutId(int turnoutId) {
        this.turnoutId = turnoutId;
    }

    public Map<String, SwitchState> getStates() {
        return states;
    }

    public void setStates(Map<String, SwitchState> states) {
        this.states = states;
    }
}
