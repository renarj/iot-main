package com.oberasoftware.max.web.api.model;

import java.util.Map;

/**
 * @author renarj
 */
public interface Item extends HomeEntity {
    Map<String, String> getProperties();
}
