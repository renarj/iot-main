package com.oberasoftware.iot.core.train.model;

import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.train.StepMode;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@JasDBEntity(bagName = "locomotives")
public class Locomotive implements IotBaseEntity {

    public static final String LOC_ADDRESS = "locAddress";
    public static final String DCC_MODE = "dccMode";
    public static final String SPEED_ATTR = "speed";
    public static final String DIRECTION_ATTR = "direction";
    public static final String LOCOMOTIVE_TYPE = "locomotive";

    private String entityId;

    private int locAddress;

    private String controllerId;

    private String thingId;

    private String commandStation;

    private String name;

    private StepMode stepMode;

    private Map<String, String> attributes = new HashMap<>();

    private List<LocFunction> functions = new ArrayList<>();

    public Locomotive(int locAddress, String controllerId, String thingId, String name) {
        this.locAddress = locAddress;
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.name = name;
    }

    public Locomotive() {
    }

    @Override
    @Id
    @JasDBProperty
    public String getId() {
        return this.entityId;
    }

    public void setId(String entityId) {
        this.entityId = entityId;
    }

    @JasDBProperty
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }



    @JasDBProperty
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    @JasDBProperty
    public int getLocAddress() {
        return locAddress;
    }

    public void setLocAddress(int locAddress) {
        this.locAddress = locAddress;
    }

    @JasDBProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JasDBProperty
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attribute, String value) {
        this.attributes.put(attribute, value);
    }

    @JasDBProperty
    public List<LocFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<LocFunction> functions) {
        this.functions = functions;
    }

    public void addFunction(LocFunction function) {
        this.functions.add(function);
    }

    @JasDBProperty
    public String getCommandStation() {
        return commandStation;
    }

    public void setCommandStation(String commandStation) {
        this.commandStation = commandStation;
    }

    @JasDBProperty
    public StepMode getStepMode() {
        return stepMode;
    }

    public void setStepMode(StepMode stepMode) {
        this.stepMode = stepMode;
    }

    @Override
    public String toString() {
        return "Locomotive{" +
                "entityId='" + entityId + '\'' +
                ", locAddress=" + locAddress +
                ", controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", commandStation='" + commandStation + '\'' +
                ", name='" + name + '\'' +
                ", attributes=" + attributes +
                ", functions=" + functions +
                '}';
    }
}
