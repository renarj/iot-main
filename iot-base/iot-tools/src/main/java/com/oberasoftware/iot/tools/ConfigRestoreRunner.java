package com.oberasoftware.iot.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ConfigRestoreRunner extends BaseRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigRestoreRunner.class);

    public boolean restorePlugins(RemoteConfig remoteConfig) {
        configure(remoteConfig);

        var loadedPlugins = loadGeneric(remoteConfig.getTargetLocation(), PluginImpl.class);
        LOG.info("Loaded plugins from disk: {}", loadedPlugins);
        genericPost(loadedPlugins, i -> client.createOrUpdatePlugin(i));
        return true;
    }

    public boolean restoreSchemas(RemoteConfig remoteConfig) {
        configure(remoteConfig);

        var loadedSchemas = loadGeneric(remoteConfig.getTargetLocation(), ThingSchemaImpl.class);
        LOG.info("Loaded Schemas from disk: {}", loadedSchemas);
        genericPost(loadedSchemas, i -> client.createOrUpdateThingSchema(i));

        return true;
    }

    public boolean restoreControllers(RemoteConfig remoteConfig) {
        configure(remoteConfig);

        List<ControllerImpl> controllers = loadGeneric(remoteConfig.getTargetLocation(), ControllerImpl.class);
        LOG.info("Loaded controllers from disk: {}", controllers);
        genericPost(controllers, i -> client.createOrUpdate(i));
        return true;
    }

    public boolean restoreThings(RemoteConfig remoteConfig) {
        configure(remoteConfig);

        List<IotThingImpl> things = loadGeneric(remoteConfig.getTargetLocation(), IotThingImpl.class);
        LOG.info("Loaded Things from disk: {}", things);
        genericPost(things, i -> client.createOrUpdate(i));
        return true;
    }

    private <T> void genericPost(List<T> items, Action<T> action) {
        items.forEach(i -> {
            LOG.info("Posting {}", i);
            try {
                action.accept(i);
            } catch (IOTException e) {
                LOG.error("Could not restore " + i.getClass() + " to remote server", e);
            }
        });
    }

    private interface Action<T> {
        void accept(T i) throws IOTException;
    }

    private <T> List<T> loadGeneric(String storageLocation, Class<T> arrayType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + arrayType.getName() + ";");
            T[] objectArray = mapper.readValue(new File(storageLocation), arrayClass);
            return Arrays.asList(objectArray);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
