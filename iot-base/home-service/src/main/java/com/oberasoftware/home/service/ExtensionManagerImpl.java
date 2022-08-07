package com.oberasoftware.home.service;

import com.oberasoftware.iot.core.extensions.AutomationExtension;
import com.oberasoftware.iot.core.extensions.ThingExtension;
import com.oberasoftware.iot.core.extensions.ExtensionManager;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.storage.HomeDAO;
import com.oberasoftware.iot.core.ControllerConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private DeviceManager deviceManager;

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ControllerConfiguration controllerConfiguration;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public List<AutomationExtension> getExtensions() {
        return extensions;
    }

    @Override
    public Optional<AutomationExtension> getExtension(String extensionId) {
        return extensions.stream().filter(x -> x.getId().equals(extensionId)).findFirst();
    }

    @Override
    public void activateController(String controllerId) throws IOTException {
        itemManager.createOrUpdateController(controllerId);
    }

    @Override
    public void activateExtensions() throws IOTException {
        LOG.info("Activating all installed extensions");
        if(extensions != null) {
            extensions.forEach(this::activateExtension);
        }
    }

    private void activateExtension(AutomationExtension extension)  {
        executorService.submit(() -> {
            LOG.info("Registering extension: {}", extension);
            itemManager.createOrUpdateThing(controllerConfiguration.getControllerId(), extension.getId(), extension.getName(), controllerConfiguration.getControllerId(), extension.getProperties());

            Optional<IotThing> thing = homeDAO.findThing(controllerConfiguration.getControllerId(), extension.getId());
            LOG.info("Activating plugin: {}", thing);
            thing.ifPresentOrElse(extension::activate, extension::activate);

            while (!extension.isReady()) {
                LOG.debug("Extension: {} is not ready yet", extension.getId());
                sleepUninterruptibly(1, TimeUnit.SECONDS);
            }

            if (extension instanceof ThingExtension) {
                registerDevices((ThingExtension) extension);
            }

            return null;
        });
    }

    private void registerDevices(ThingExtension deviceExtension) {
        List<IotThing> things = deviceExtension.getThings();
        things.forEach(t -> {
            try {
                deviceManager.registerThing(t);
            } catch (IOTException e) {
                throw new RuntimeIOTException("Unable to store plugin devices", e);
            }
        });
    }


}
