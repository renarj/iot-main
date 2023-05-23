package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author renarj
 */
@JasDBEntity(bagName = "devices")
public class IotThingImpl implements IotThing {
    private String id;
    private String thingId;
    private String controllerId;
    private String friendlyName;

    private String type = "default";

    private String pluginId;

    private String parentId;

    private Set<String> attributes = new HashSet<>();

    private Map<String, String> properties;

    protected IotThingImpl(String thingId, String controllerId) {
        this.thingId = thingId;
        this.controllerId = controllerId;
    }
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

    @JasDBProperty
    @Override
    public Set<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attribute) {
        this.attributes.add(attribute);
    }

    @Override
    @JasDBProperty
    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    @JasDBProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String getProperty(String key) {
        return this.properties.get(key);
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
        return "IotThingImpl{" +
                "id='" + id + '\'' +
                ", thingId='" + thingId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", type='" + type + '\'' +
                ", pluginId='" + pluginId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", attributes=" + attributes +
                ", properties=" + properties +
                '}';
    }
}
