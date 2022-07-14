package com.oberasoftware.iot.core.model.storage;

/**
 * @author Renze de Vries
 */
public interface PluginItem extends Item {
    String getName();

    String getControllerId();

    String getPluginId();
}
