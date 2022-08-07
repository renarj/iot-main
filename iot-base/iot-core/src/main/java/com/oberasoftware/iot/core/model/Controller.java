package com.oberasoftware.iot.core.model;

import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface Controller extends IotBaseEntity {
    String getControllerId();

    Map<String, String> getProperties();

    String getOrgId();
}
