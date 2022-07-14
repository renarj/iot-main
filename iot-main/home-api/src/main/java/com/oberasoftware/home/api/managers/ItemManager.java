package com.oberasoftware.home.api.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.ControllerItem;
import com.oberasoftware.iot.core.model.storage.DeviceItem;
import com.oberasoftware.iot.core.model.storage.Item;
import com.oberasoftware.iot.core.model.storage.PluginItem;

import java.util.List;
import java.util.Map;

/**
 * @author renarj
 */
public interface ItemManager {
    ControllerItem createOrUpdateController(String controllerId) throws IOTException;

    PluginItem createOrUpdatePlugin(String controllerId, String pluginId, String name, Map<String, String> properties) throws IOTException;

    DeviceItem createOrUpdateDevice(String controllerId, String pluginId, String deviceId, String name, Map<String, String> properties) throws IOTException;

    List<ControllerItem> findControllers();

    List<PluginItem> findPlugins(String controllerId);

    List<DeviceItem> findDevices(String controllerId, String pluginId);

    List<DeviceItem> findDevices(String controllerId);

    Item findItem(String id);
}
