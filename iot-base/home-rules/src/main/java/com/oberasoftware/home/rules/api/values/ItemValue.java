package com.oberasoftware.home.rules.api.values;

import java.util.Objects;

/**
 * @author Renze de Vries
 */
public class ItemValue implements ResolvableValue {

    private String thingId;
    private String controllerId;
    private String label;

    public ItemValue(String controllerId, String thingId, String label) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.label = label;
    }

    public ItemValue() {
    }

    public String getThingId() {
        return thingId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ItemValue{" +
                "thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemValue itemValue = (ItemValue) o;
        return Objects.equals(thingId, itemValue.thingId) && Objects.equals(controllerId, itemValue.controllerId) && Objects.equals(label, itemValue.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thingId, controllerId, label);
    }
}
