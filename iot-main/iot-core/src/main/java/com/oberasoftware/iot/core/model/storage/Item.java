package com.oberasoftware.iot.core.model.storage;

import com.oberasoftware.iot.core.model.IotBaseEntity;

import java.util.Map;

/**
 * @author renarj
 */
public interface Item extends IotBaseEntity {
    Map<String, String> getProperties();
}
