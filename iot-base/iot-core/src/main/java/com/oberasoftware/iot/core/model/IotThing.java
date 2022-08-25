package com.oberasoftware.iot.core.model;

import java.util.Map;

/**
 * @author renarj
 */
public interface IotThing extends IotBaseEntity {
    String getThingId();

    String getControllerId();

    String getPluginId();

    String getFriendlyName();

    String getParentId();

    Map<String, String> getProperties();
}
