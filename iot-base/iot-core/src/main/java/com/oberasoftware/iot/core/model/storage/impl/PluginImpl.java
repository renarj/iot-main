package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity(name="Plugins")
@JasDBEntity(bagName = "plugins")
public class PluginImpl implements Plugin {
    @Column(unique = true)
    private String pluginId;
    private String name;

    @javax.persistence.Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    public PluginImpl(String pluginId, String name, String id) {
        this.pluginId = pluginId;
        this.name = name;
        this.id = id;
    }

    public PluginImpl() {
    }

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
    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    @JasDBProperty
    public String getFriendlyName() {
        return name;
    }

    public void setFriendlyName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PluginImpl{" +
                "pluginId='" + pluginId + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
