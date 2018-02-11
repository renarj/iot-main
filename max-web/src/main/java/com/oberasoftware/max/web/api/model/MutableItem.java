package com.oberasoftware.max.web.api.model;

import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface MutableItem extends Item {
    void setProperties(Map<String, String> properties);

    void setId(String id);
}
