package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.Map;

/**
 * @author renarj
 */
@JasDBEntity(bagName = "devices")
public class IotThingImpl implements IotThing {
    private String id;
    private String thingId;
    private String controllerId;

    private String friendlyName;

    private String pluginId;

    private String parentId;

    private Map<String, String> properties;

    public IotThingImpl(String id, String controllerId, String thingId, String friendlyName, String pluginId, String parent, Map<String, String> properties) {
        this.controllerId = controllerId;
        this.id = id;
        this.thingId = thingId;
        this.parentId = parent;
        this.friendlyName = friendlyName;
        this.pluginId = pluginId;
        this.properties = properties;
    }

    public IotThingImpl(String controllerId, String thingId, String friendlyName, String pluginId, String parent, Map<String, String> properties) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.parentId = parent;
        this.pluginId = pluginId;
        this.friendlyName = friendlyName;
        this.properties = properties;
    }

    public IotThingImpl() {
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
    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

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

    @JasDBProperty
    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @JasDBProperty
    @Override
    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    @Override
    public String toString() {
        return "DeviceItemImpl{" +
                "id='" + id + '\'' +
                ", thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", pluginId='" + pluginId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", properties=" + properties +
                '}';
    }
}
