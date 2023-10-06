package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.SystemDataManager;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemDataRestService {
    private static final Logger LOG = LoggerFactory.getLogger(SystemDataRestService.class);

    private final SystemDataManager systemDataManager;

    public SystemDataRestService(SystemDataManager systemDataManager) {
        this.systemDataManager = systemDataManager;
    }

    @RequestMapping(value = "/plugins({pluginId})/schemas", method = RequestMethod.GET)
    public List<ThingSchema> getSchemas(@PathVariable String pluginId) {
        LOG.debug("Requested All Templates for plugin: {}", pluginId);

        return systemDataManager.findSchemas(pluginId);
    }

    @RequestMapping("/plugins({pluginId})/schemas({schemaId})")
    public ResponseEntity<ThingSchema> findSchema(@PathVariable String pluginId, @PathVariable String schemaId) {
        var oTemplate = systemDataManager.findSchema(pluginId, schemaId);

        return oTemplate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/plugins", method = RequestMethod.GET)
    public List<Plugin> getPluginsAvailable() {
        LOG.debug("Requested all available plugin names");
        return systemDataManager.findPlugins();
    }

    @RequestMapping(value = "/plugins", method = RequestMethod.POST)
    public ResponseEntity<Object> createPlugin(@RequestBody PluginImpl plugin) {
        if(StringUtils.hasText(plugin.getPluginId()) && StringUtils.hasText(plugin.getFriendlyName())) {
            LOG.info("Creating Plugin: '{}'", plugin);
            var createdPlugin = systemDataManager.storePlugin(plugin);

            return new ResponseEntity<>(createdPlugin, HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity PluginId or Name is missing: '{}'", plugin);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/schemas", method = RequestMethod.POST)
    public ResponseEntity<Object> createSchema(@RequestBody ThingSchemaImpl schema) {
        if(StringUtils.hasText(schema.getPluginId()) && StringUtils.hasText(schema.getSchemaId())) {
            LOG.info("Creating Schema: '{}'", schema);
            var createdSchema = systemDataManager.storeScheme(schema);

            return new ResponseEntity<>(createdSchema, HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity PluginId or SchemaId is missing: '{}'", schema);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/plugins({pluginId})/schemas({schemaId})", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSchema(@PathVariable String pluginId, @PathVariable String schemaId) throws IOTException {
        if(StringUtils.hasText(pluginId) && StringUtils.hasText(schemaId)) {
            LOG.info("Deleting Schema for plugin: {} and schemaId: {}", pluginId, schemaId);
            var result = systemDataManager.removeSchema(pluginId, schemaId);

            if(result) {
                return ResponseEntity.accepted().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            LOG.warn("Invalid entity PluginId or schemaId is missing: '{}/{}'", pluginId, schemaId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/plugins({pluginId})", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deletePlugin(@PathVariable String pluginId) throws IOTException {
        if(StringUtils.hasText(pluginId)) {
            LOG.info("Deleting Plugin: {}", pluginId);
            var result = systemDataManager.removePlugin(pluginId);

            if(result) {
                return ResponseEntity.accepted().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            LOG.warn("Invalid PluginId: {}", pluginId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
