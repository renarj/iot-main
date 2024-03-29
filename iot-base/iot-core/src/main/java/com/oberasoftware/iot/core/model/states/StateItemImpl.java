package com.oberasoftware.iot.core.model.states;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author renarj
 */
public class StateItemImpl implements StateItem {

    private String label;

    @JsonDeserialize(as = ValueImpl.class)
    private Value value;

    public StateItemImpl(String label, Value value) {
        this.label = label;
        this.value = value;
    }

    public StateItemImpl() {
    }

    @Override
    public String getAttribute() {
        return label;
    }

    public void setAttribute(String label) {
        this.label = label;
    }

    @Override
    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StateItemImpl{" +
                "label='" + label + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateItemImpl stateItem = (StateItemImpl) o;

        if (!label.equals(stateItem.label)) return false;
        return value.equals(stateItem.value);

    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
