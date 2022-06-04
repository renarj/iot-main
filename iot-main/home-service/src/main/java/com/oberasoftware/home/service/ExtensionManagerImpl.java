package com.oberasoftware.home.service;

import com.oberasoftware.home.api.exceptions.HomeAutomationException;
import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import com.oberasoftware.home.api.extensions.AutomationExtension;
import com.oberasoftware.home.api.extensions.DeviceExtension;
import com.oberasoftware.home.api.extensions.ExtensionManager;
import com.oberasoftware.home.api.managers.DeviceManager;
import com.oberasoftware.home.api.managers.ItemManager;
import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.home.api.model.storage.PluginItem;
import com.oberasoftware.home.api.storage.HomeDAO;
import com.oberasoftware.home.core.ControllerConfiguration;
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
    public void activateController(String controllerId) throws HomeAutomationException {
        itemManager.createOrUpdateController(controllerId);
    }

    @Override
    public void activateExtensions() throws HomeAutomationException {
        LOG.info("Activating all installed extensions");
        if(extensions != null) {
            extensions.forEach(this::activateExtension);
        }
    }

    private void activateExtension(AutomationExtension extension)  {
        executorService.submit(() -> {
            LOG.info("Registering extension: {}", extension);
            itemManager.createOrUpdatePlugin(controllerConfiguration.getControllerId(), extension.getId(), extension.getName(), extension.getProperties());

            Optional<PluginItem> pluginItem = homeDAO.findPlugin(controllerConfiguration.getControllerId(), extension.getId());
            LOG.info("Activating plugin: {}", pluginItem);
            extension.activate(pluginItem);

            while (!extension.isReady()) {
                LOG.debug("Extension: {} is not ready yet", extension.getId());
                sleepUninterruptibly(1, TimeUnit.SECONDS);
            }

            if (extension instanceof DeviceExtension) {
                registerDevices((DeviceExtension) extension);
            }

            return null;
        });
    }

    private void registerDevices(DeviceExtension deviceExtension) {
        List<Device> devices = deviceExtension.getDevices();
        devices.forEach(d -> {
            try {
                deviceManager.registerDevice(deviceExtension.getId(), d);
            } catch (HomeAutomationException e) {
                throw new RuntimeHomeAutomationException("Unable to store plugin devices", e);
            }
        });
    }


}
