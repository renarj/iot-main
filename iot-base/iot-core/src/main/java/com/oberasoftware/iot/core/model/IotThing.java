package com.oberasoftware.iot.core.model;

import java.util.Map;
import java.util.Set;

/**
 * @author renarj
 */
public interface IotThing extends IotBaseEntity {
    String getThingId();

    String getControllerId();

    String getPluginId();

    String getFriendlyName();

    Set<String> getAttributes();

    String getProperty(String key);

    String getParentId();

    Map<String, String> getProperties();
}
