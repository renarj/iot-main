package com.oberasoftware.iot.core.model;

import com.oberasoftware.iot.core.model.storage.impl.AttributeType;
import com.oberasoftware.iot.core.model.storage.impl.SchemaFieldDescriptor;

import java.util.Map;

public interface ThingSchema extends IotBaseEntity {
    String getSchemaId();

    String getPluginId();

    String getType();

    String getTemplate();

    Map<String, SchemaFieldDescriptor> getProperties();

    Map<String, AttributeType> getAttributes();
}
