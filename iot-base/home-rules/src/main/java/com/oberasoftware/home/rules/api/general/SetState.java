package com.oberasoftware.home.rules.api.general;

import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.api.values.ResolvableValue;

/**
 * @author Renze de Vries
 */
public class SetState implements Statement {

    private ThingAttributeValue thingAttributeValue;
    private ResolvableValue resolvableValue;

    public SetState(ThingAttributeValue thingAttributeValue, ResolvableValue resolvableValue) {
        this.thingAttributeValue = thingAttributeValue;
        this.resolvableValue = resolvableValue;
    }

    public SetState() {
    }

    public ThingAttributeValue getItemValue() {
        return thingAttributeValue;
    }

    public void setItemValue(ThingAttributeValue thingAttributeValue) {
        this.thingAttributeValue = thingAttributeValue;
    }

    public ResolvableValue getResolvableValue() {
        return resolvableValue;
    }

    public void setResolvableValue(ResolvableValue resolvableValue) {
        this.resolvableValue = resolvableValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetState that = (SetState) o;

        if (!thingAttributeValue.equals(that.thingAttributeValue)) return false;
        return resolvableValue.equals(that.resolvableValue);

    }

    @Override
    public int hashCode() {
        int result = thingAttributeValue.hashCode();
        result = 31 * result + resolvableValue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SetStateAction{" +
                "itemValue=" + thingAttributeValue +
                ", resolvableValue=" + resolvableValue +
                '}';
    }
}

