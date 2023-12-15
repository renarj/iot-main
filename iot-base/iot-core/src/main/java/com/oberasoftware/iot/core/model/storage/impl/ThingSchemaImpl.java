package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.LinkedHashMap;
import java.util.Map;

@JasDBEntity(bagName = "templates")
public class ThingSchemaImpl implements ThingSchema {
    private String schemaId;

    private String pluginId;

    private String template;

    private String type;

    private Map<String, SchemaFieldDescriptor> properties = new LinkedHashMap<>();

    private String id;

    private Map<String, AttributeType> attributes = new LinkedHashMap<>();

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
    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    @Override
    @JasDBProperty
    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    @Override
    @JasDBProperty
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    @JasDBProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    @JasDBProperty
    public Map<String, SchemaFieldDescriptor> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SchemaFieldDescriptor> properties) {
        this.properties = properties;
    }

    public void addProperty(String field, SchemaFieldDescriptor type) {
        this.properties.put(field, type);
    }

    @Override
    @JasDBProperty
    public Map<String, AttributeType> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, AttributeType> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ThingSchemaImpl{" +
                "schemaId='" + schemaId + '\'' +
                ", pluginId='" + pluginId + '\'' +
                ", template='" + template + '\'' +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                ", id='" + id + '\'' +
                '}';
    }
}

