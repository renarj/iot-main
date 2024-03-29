package com.oberasoftware.iot.core.legacymodel;

import com.oberasoftware.iot.core.model.states.Value;

/**
 * @author Renze de Vries
 */
public class OnOffValue implements Value {

    public static String LABEL = "on";

    private final boolean on;

    public OnOffValue(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    public VALUE_TYPE getType() {
        return VALUE_TYPE.STRING;
    }

    @Override
    public String getValue() {
        return asString();
    }

    @Override
    public String asString() {
        return isOn() ? "true" : "false";
    }

    @Override
    public String toString() {
        return "OnOffValue{" +
                "on=" + on +
                '}';
    }
}
