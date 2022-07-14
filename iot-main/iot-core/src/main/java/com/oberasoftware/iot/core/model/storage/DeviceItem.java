package com.oberasoftware.iot.core.model.storage;

/**
 * @author Renze de Vries
 */
public interface DeviceItem extends Item {
    String getPluginId();

    String getDeviceId();

    String getName();

    String getControllerId();
}
