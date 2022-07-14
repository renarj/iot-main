package com.oberasoftware.iot.core.model;

import java.util.Map;

/**
 * @author renarj
 */
public interface IotThing extends IotBaseEntity {
    String getControllerId();

    Map<String, String> getProperties();
}
