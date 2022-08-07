package com.oberasoftware.iot.core.model;

import java.util.Map;

/**
 * @author renarj
 */
public interface IotThing extends IotBaseEntity {
    String getControllerId();

    String getPluginId();

    String getParentId();

    Map<String, String> getProperties();
}
