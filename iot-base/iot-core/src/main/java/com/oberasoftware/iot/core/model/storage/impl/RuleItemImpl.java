package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.storage.RuleItem;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.Map;

/**
 * @author Renze de Vries
 */
@JasDBEntity(bagName = "rules")
public class RuleItemImpl implements RuleItem {
    private String id;
    private String name;
    private String controllerId;
    private String blocklyData;

    private Map<String, String> properties;

    public RuleItemImpl() {
    }

    public RuleItemImpl(String id, String name, String controllerId, String blocklyData, Map<String, String> properties) {
        this.id = id;
        this.name = name;
        this.controllerId = controllerId;
        this.blocklyData = blocklyData;
        this.properties = properties;
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
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
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
    public String getBlocklyData() {
        return blocklyData;
    }

    public void setBlocklyData(String blocklyData) {
        this.blocklyData = blocklyData;
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
    public String toString() {
        return "RuleItemImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", blocklyData='" + blocklyData + '\'' +
                ", properties=" + properties +
                '}';
    }
}
