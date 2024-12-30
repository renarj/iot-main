package com.oberasoftware.iot.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.oberasoftware.iot.tools.ConfigBackupRunner.URL_FORMAT;

@Component
public class ConfigRestoreRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigRestoreRunner.class);

    @Autowired
    private AgentClient client;

    public boolean restorePlugins(RemoteConfig remoteConfig) {
        client.configure(String.format(URL_FORMAT, remoteConfig.getTargetHost(), remoteConfig.getTargetPort()), remoteConfig.getTargetLocation());

        var loadedPlugins = loadPlugins(remoteConfig.getTargetLocation());
        LOG.info("Loaded plugins from disk: {}", loadedPlugins);
        loadedPlugins.forEach(p -> {
            LOG.info("Posting Plugin: {} to server: {}", p, remoteConfig.getTargetHost());
            try {
                client.createOrUpdatePlugin(p);
            } catch (IOTException e) {
                LOG.error("Could not restore Plugin to remote server", e);
            }
        });
        return true;
    }

    public boolean restoreSchemas(RemoteConfig remoteConfig) {
        client.configure(String.format(URL_FORMAT, remoteConfig.getTargetHost(), remoteConfig.getTargetPort()), remoteConfig.getTargetLocation());

        var loadedSchemas = loadSchemas(remoteConfig.getTargetLocation());
        LOG.info("Loaded Schemas from disk: {}", loadedSchemas);

        loadedSchemas.forEach(s -> {
            LOG.info("Posting schema: {} to server: {}", s, remoteConfig.getTargetHost());
            try {
                client.createOrUpdateThingSchema(s);
            } catch (IOTException e) {
                LOG.error("Could not restore schema to remote server", e);
            }
        });

        return true;
    }

    private List<Plugin> loadPlugins(String storageLocation) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            var plugins = mapper.readValue(new File(storageLocation), PluginImpl[].class);
            return Lists.newArrayList(plugins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ThingSchema> loadSchemas(String storageLocation) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            var schemas = mapper.readValue(new File(storageLocation), ThingSchemaImpl[].class);
            return Lists.newArrayList(schemas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
