package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;

import java.util.List;
import java.util.Optional;

public interface SystemDataManager {
    PluginImpl storePlugin(PluginImpl plugin);

    ThingSchemaImpl storeScheme(ThingSchemaImpl template);

    boolean removeSchema(String pluginId, String schemaId) throws IOTException;

    boolean removePlugin(String pluginId) throws IOTException;

    List<Plugin> findPlugins();

    List<ThingSchema> findSchemas(String pluginId);

    Optional<ThingSchema> findSchema(String pluginId, String schemaId);
}
