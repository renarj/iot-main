package com.oberasoftware.iot.core.model.storage;

import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface MutableItem extends PropertiesContainer {
    void setProperties(Map<String, String> properties);

    void setId(String id);
}
