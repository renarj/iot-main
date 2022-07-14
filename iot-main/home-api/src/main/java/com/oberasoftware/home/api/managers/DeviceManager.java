package com.oberasoftware.home.api.managers;

import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.DeviceItem;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface DeviceManager {
    DeviceItem registerDevice(String pluginId, Device device) throws IOTException;

    DeviceItem findDevice(String itemId);

    List<DeviceItem> getDevices(String controllerId);

    Optional<DeviceItem> findDeviceItem(String controllerId, String pluginId, String deviceId);
}
