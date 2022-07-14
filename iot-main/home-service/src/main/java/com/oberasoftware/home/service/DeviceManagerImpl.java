package com.oberasoftware.home.service;

import com.oberasoftware.home.api.managers.DeviceManager;
import com.oberasoftware.home.api.managers.ItemManager;
import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.iot.core.model.storage.DeviceItem;
import com.oberasoftware.iot.core.model.storage.PluginItem;
import com.oberasoftware.home.api.storage.HomeDAO;
import com.oberasoftware.home.core.ControllerConfiguration;
import com.oberasoftware.home.core.model.storage.DeviceItemImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class DeviceManagerImpl implements DeviceManager {
    private static final Logger LOG = getLogger(DeviceManagerImpl.class);

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ControllerConfiguration controllerConfiguration;

    @Override
    public DeviceItem registerDevice(String pluginId, Device device) throws HomeAutomationException {
        LOG.debug("Registering device: {} for plugin: {}", device, pluginId);
        String controllerId = controllerConfiguration.getControllerId();

        Optional<PluginItem> plugin = homeDAO.findPlugin(controllerId, pluginId);
        if(plugin.isPresent()) {
            return itemManager.createOrUpdateDevice(controllerConfiguration.getControllerId(), plugin.get().getPluginId(), device.getId(), device.getName(), device.getProperties());
        } else {
            LOG.error("Unable to register device: {} for plugin: {}", device, pluginId);
            return null;
        }
    }

    @Override
    public DeviceItem findDevice(String deviceId) {
        Optional<DeviceItemImpl> d =  homeDAO.findItem(DeviceItemImpl.class, deviceId);
        return d.get();
    }

    @Override
    public Optional<DeviceItem> findDeviceItem(String controllerId, String pluginId, String deviceId) {
        return homeDAO.findDevice(controllerId, pluginId, deviceId);
    }

    @Override
    public List<DeviceItem> getDevices(String controllerId) {
        return homeDAO.findDevices(controllerId);
    }

}
