package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.ThingTemplate;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

import java.util.Set;

@JasDBEntity(bagName = "templates")
public class ThingTemplateImpl implements ThingTemplate {
    private String templateId;

    private String pluginId;

    private String template;

    private Set<String> properties;

    private String id;

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
    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
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
    public Set<String> getProperties() {
        return properties;
    }

    public void setProperties(Set<String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ThingTemplateImpl{" +
                "templateId='" + templateId + '\'' +
                ", pluginId='" + pluginId + '\'' +
                ", template='" + template + '\'' +
                ", properties=" + properties +
                ", id='" + id + '\'' +
                '}';
    }
}

