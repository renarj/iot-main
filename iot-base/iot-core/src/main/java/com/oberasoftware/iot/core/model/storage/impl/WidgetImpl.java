package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.storage.Widget;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
@JasDBEntity(bagName = "widgets")
public class WidgetImpl implements Widget {
    private String id;
    private String name;
    private String widgetType;

    private String containerId;

    private String thingId;

    private String controllerId;

    private long weight = 0;

    private Map<String, String> properties = new HashMap<>();

    public WidgetImpl(String id, String name, String containerId, String widgetType, String thingId, String controllerId, Map<String, String> properties, long weight) {
        this.id = id;
        this.name = name;
        this.containerId = containerId;
        this.widgetType = widgetType;
        this.thingId = thingId;
        this.controllerId = controllerId;
        this.properties = properties;
        this.weight = weight;
    }

    public WidgetImpl() {
    }

    @Override
    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    @JasDBProperty
    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    @Override
    @JasDBProperty
    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    @Override
    @JasDBProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JasDBProperty
    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }

    @Override
    @JasDBProperty
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    @Override
    @JasDBProperty
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    @JasDBProperty
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(String property, String value) {
        this.properties.put(property, value);
    }

    @Override
    public String toString() {
        return "WidgetImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", widgetType='" + widgetType + '\'' +
                ", containerId='" + containerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", weight=" + weight +
                ", properties=" + properties +
                '}';
    }
}
