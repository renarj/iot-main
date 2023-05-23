package com.oberasoftware.iot.core.train.model;

import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

@JasDBEntity(bagName = "locFunction")
public class LocFunction {
    private int functionNumber;

    private String functionType;

    private String description;

    private boolean toggle;

    private boolean enabled;

    public LocFunction(int functionNumber, String functionType, String description, boolean toggle, boolean enabled) {
        this.functionNumber = functionNumber;
        this.functionType = functionType;
        this.description = description;
        this.toggle = toggle;
        this.enabled = enabled;
    }

    public LocFunction() {
    }

    @JasDBProperty
    public int getFunctionNumber() {
        return functionNumber;
    }

    public void setFunctionNumber(int functionNumber) {
        this.functionNumber = functionNumber;
    }

    @JasDBProperty
    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    @JasDBProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JasDBProperty
    public boolean isToggle() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    @JasDBProperty
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
