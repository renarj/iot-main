package com.oberasoftware.home.agent.core.extension;

import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.ExtensionManager;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ExtensionManagerImpl implements ExtensionManager {
    private static final Logger LOG = getLogger(ExtensionManagerImpl.class);

    @Autowired(required = false)
    private List<AutomationExtension> extensions;

//    @Autowired
//    private DeviceManager deviceManager;

    @Autowired
    private ThingClient thingClient;

//    @Autowired
//    private HomeDAO homeDAO;

//    @Autowired
//    private ItemManager itemManager;

    @Autowired
    private AgentControllerInformation controllerConfiguration;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public List<AutomationExtension> getExtensions() {
        return extensions;
    }

    @Override
    public Optional<AutomationExtension> getExtensionById(String extensionId) {
        return extensions.stream().filter(x -> x.getId().equals(extensionId)).findFirst();
    }

    @Override
    public Optional<AutomationExtension> getExtensionByName(String extensionName) {
        return extensions.stream().filter(x -> x.getName().equals(extensionName)).findFirst();
    }

    @Override
    public void activateController(String controllerId) throws IOTException {
        thingClient.createOrUpdate(new ControllerImpl(controllerId, null, new HashMap<>()));
    }

    @Override
    public void activateExtensions() throws IOTException {
        LOG.info("Activating all installed extensions");
        if(extensions != null) {
            extensions.forEach(this::activateExtension);
        }
    }

    private void activateExtension(AutomationExtension extension)  {
        var controllerId = controllerConfiguration.getControllerId();

        executorService.submit(() -> {
            LOG.info("Registering extension: {}", extension);
            try {
                var pThing = new IotThingImpl(controllerId, extension.getId(),
                        extension.getName(), extension.getId(), controllerId, extension.getProperties());
                pThing.setType("plugin");
                var pluginThing = thingClient.createOrUpdate(pThing);

                LOG.info("Activating plugin: {}", pluginThing);
                extension.activate(pluginThing);

                while (!extension.isReady()) {
                    LOG.debug("Extension: {} is not ready yet", extension.getId());
                    sleepUninterruptibly(1, TimeUnit.SECONDS);
                }

                extension.discoverThings(thing -> {
                    try {
                        thingClient.createOrUpdate(thing);
                    } catch (IOTException e) {
                        throw new RuntimeIOTException("Unable to store Thing: " + thing, e);
                    }

                });
            } catch(IOTException e) {
                LOG.error("Unable to active extension", e);
            }
        });
    }
}
