package com.oberasoftware.iot.core.model;

import com.oberasoftware.iot.core.model.storage.impl.AttributeType;

import java.util.Map;

/**
 * @author renarj
 */
public interface IotThing extends IotBaseEntity {
    String getThingId();

    String getControllerId();

    String getPluginId();

    String getType();

    String getSchemaId();

    String getFriendlyName();

    Map<String, AttributeType> getAttributes();

    String getProperty(String key);

    String getParentId();

    Map<String, String> getProperties();
}
