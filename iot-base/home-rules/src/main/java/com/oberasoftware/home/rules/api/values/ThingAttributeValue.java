package com.oberasoftware.home.rules.api.values;

/**
 * @author Renze de Vries
 */
public class ThingAttributeValue implements ResolvableValue {

    private String thingId;
    private String controllerId;
    private String attribute;

    public ThingAttributeValue(String controllerId, String thingId, String attribute) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.attribute = attribute;
    }

    public ThingAttributeValue() {
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

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "ThingAttributeValue{" +
                "thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
