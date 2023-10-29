package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.storage.TemplateFieldType;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

@JasDBEntity(bagName = "fieldDescriptors")
public class SchemaFieldDescriptor {

    private TemplateFieldType fieldType;
    private String defaultValue;

    public SchemaFieldDescriptor(TemplateFieldType fieldType, String defaultValue) {
        this.fieldType = fieldType;
        this.defaultValue = defaultValue;
    }

    public SchemaFieldDescriptor() {
    }

    @JasDBProperty
    public TemplateFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(TemplateFieldType fieldType) {
        this.fieldType = fieldType;
    }


    @JasDBProperty
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "SchemaFieldDescriptorImpl{" +
                "fieldType=" + fieldType +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
