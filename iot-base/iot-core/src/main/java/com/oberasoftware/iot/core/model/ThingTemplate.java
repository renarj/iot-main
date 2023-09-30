package com.oberasoftware.iot.core.model;

import java.util.Set;

public interface ThingTemplate extends IotBaseEntity {
    String getTemplateId();

    String getPluginId();

    String getTemplate();

    Set<String> getProperties();
}
