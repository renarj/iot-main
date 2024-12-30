package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.managers.SystemDataManager;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SystemDataManagerImpl implements SystemDataManager {
    private static final Logger LOG = LoggerFactory.getLogger(SystemDataManagerImpl.class);

    private final HomeDAO homeDAO;

    private final CentralDatastore centralDatastore;

    public SystemDataManagerImpl(HomeDAO homeDAO, CentralDatastore centralDatastore) {
        this.homeDAO = homeDAO;
        this.centralDatastore = centralDatastore;
    }

    @Override
    public List<Plugin> findPlugins() {
        LOG.info("Finding all plugins with templates");
        return homeDAO.findPlugins();
    }

    @Override
    public PluginImpl storePlugin(PluginImpl plugin) {
        centralDatastore.beginTransaction();
        try {
            var oPlugin = homeDAO.findPlugin(plugin.getPluginId());
            oPlugin.ifPresent(value -> plugin.setId(value.getId()));

            return centralDatastore.store(plugin);
        } catch (DataStoreException e) {
            throw new RuntimeException(e);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public ThingSchemaImpl storeScheme(ThingSchemaImpl schema) {
        centralDatastore.beginTransaction();
        try {
            validateSchema(schema);
            var oSchema = homeDAO.findSchema(schema.getPluginId(), schema.getSchemaId());
            oSchema.ifPresent(thingSchema -> schema.setId(thingSchema.getId()));
            return centralDatastore.store(schema);
        } catch (DataStoreException e) {
            throw new RuntimeException(e);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    private void validateSchema(ThingSchemaImpl schema) {
        throwIfNull(schema.getParentType());
        throwIfNull(schema.getType());
        throwIfNull(schema.getSchemaId());
        throwIfNull(schema.getPluginId());

        var parentType = schema.getParentType();
        if(!parentType.equalsIgnoreCase("Controller") && !parentType.equalsIgnoreCase("Plugin")) {
            if(homeDAO.findSchema(schema.getPluginId(), schema.getParentType()).isEmpty()) {
                throw new RuntimeIOTException("Schema has invalid parent Schema or Type: " + parentType);
            }
        }

        if(homeDAO.findPlugin(schema.getPluginId()).isEmpty()) {
            throw new RuntimeIOTException("Schema has invalid plugin Id: " + schema.getPluginId());
        }
    }

    private void throwIfNull(String property) {
        if(property == null || property.isEmpty()) {
            throw new RuntimeIOTException("Invalid schema, property is null or empty");
        }
    }

    @Override
    public boolean removeSchema(String pluginId, String schemaId) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<ThingSchema> schema = homeDAO.findSchema(pluginId, schemaId);
            if(schema.isPresent()) {
                LOG.debug("Deleted SChema: {} on plugin: {}", schemaId, pluginId);
                centralDatastore.delete(ThingSchemaImpl.class, schema.get().getId());
                return true;
            } else {
                LOG.warn("Tried to remove Plugin/Schema: {}/{} but was not found", pluginId, schemaId);
                return false;
            }
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public boolean removePlugin(String pluginId) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            var oPlugin = homeDAO.findPlugin(pluginId);
            if(oPlugin.isPresent()) {
                LOG.info("Removing Plugin metadata, first removing schemas for plugin: {}", pluginId);
                for (ThingSchema sc : findSchemas(pluginId)) {
                    LOG.info("Removing schema: {} on plugin: {}", sc.getSchemaId(), pluginId);
                    removeSchema(pluginId, sc.getSchemaId());
                }

                centralDatastore.delete(PluginImpl.class, oPlugin.get().getId());
                return true;
            } else {
                throw new IOTException("Could not delete plugin: " + pluginId + " was not found");
            }
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public List<ThingSchema> findSchemas(String pluginId) {
        return homeDAO.findSchemas(pluginId);
    }

    @Override
    public Optional<ThingSchema> findSchema(String pluginId, String schemaId) {
        return homeDAO.findSchema(pluginId, schemaId);
    }
}
