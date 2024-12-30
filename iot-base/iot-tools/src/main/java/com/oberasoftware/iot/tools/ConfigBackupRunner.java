package com.oberasoftware.iot.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigBackupRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigBackupRunner.class);

    @Autowired
    private AgentClient client;

    public static final String URL_FORMAT = "http://%s:%s";

    public boolean backupPlugins(RemoteConfig config) {
        client.configure(String.format(URL_FORMAT, config.getTargetHost(), config.getTargetPort()), config.getTargetLocation());

        try {
            List<Plugin> pluginList = client.getPlugins();
            LOG.info("Retrieved list of Plugins: {}", pluginList);

            doBackup(config.getTargetLocation(), pluginList);
        } catch (IOTException | IOException e) {
            LOG.error("Could not create backup", e);
            return false;
        }

        LOG.info("Plugin Backup is completed");
        return true;
    }

    public boolean backupSchemas(RemoteConfig config) {
        client.configure(String.format(URL_FORMAT, config.getTargetHost(), config.getTargetPort()), config.getTargetLocation());

        try {
            List<Plugin> pluginList = client.getPlugins();
            LOG.info("Retrieved list of Plugins: {}", pluginList);

            List<ThingSchema> schemas = new ArrayList<>();
            pluginList.forEach(p -> {
                try {
                    LOG.info("Downloading schemas for plugin: {}", p);
                    schemas.addAll(client.getSchemas(p.getPluginId()));
                } catch (IOTException e) {
                    LOG.error("Could not download configuration", e);
                }
            });
            doBackup(config.getTargetLocation(), schemas);
        } catch (IOTException | IOException e) {
            LOG.error("Could not create backup", e);
            return false;
        }

        LOG.info("Schema Backup is completed");
        return true;
    }

    private void doBackup(String targetFile, Object value) throws IOException {
        SimpleWriter writer = new SimpleWriter(targetFile);
        try {
            writer.open();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String backupContents = objectMapper.writeValueAsString(value);

            LOG.debug("Writing backup to file: {}", backupContents);
            writer.write(backupContents);
        } finally {
            writer.close();
        }

    }
}
