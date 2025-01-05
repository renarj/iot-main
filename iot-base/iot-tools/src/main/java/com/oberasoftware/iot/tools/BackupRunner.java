package com.oberasoftware.iot.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.Plugin;
import com.oberasoftware.iot.core.model.ThingSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BackupRunner extends BaseRunner {
    private static final Logger LOG = LoggerFactory.getLogger(BackupRunner.class);

    public boolean backupPlugins(RemoteConfig config) {
        configure(config);

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
        configure(config);

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

    public boolean backupControllers(RemoteConfig config) {
        configure(config);
        try {
            doBackup(config.getTargetLocation(), client.getControllers());
            return true;
        } catch (IOException | IOTException e) {
            LOG.error("Could not create backup", e);
            return false;
        }
    }

    public boolean backupThings(RemoteConfig config) {
        configure(config);
        try {
            var controllers = client.getControllers();
            List<IotThing> things = new ArrayList<>();
            controllers.forEach(controller -> {
                try {
                    things.addAll(client.getThings(controller.getControllerId()));
                } catch (IOTException e) {
                    LOG.error("Could not create backup, unable to retrieve things for controller: " + controller.getControllerId(), e);
                }
            });

            doBackup(config.getTargetLocation(), things);

            return true;
        } catch (IOException | IOTException e) {
            LOG.error("Could not create backup", e);
            return false;
        }
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
