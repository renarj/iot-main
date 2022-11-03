package com.oberasoftware.iot.core.model.storage;

/**
 * @author Renze de Vries
 */
public interface Widget extends MutableItem {
    long getWeight();

    String getContainerId();

    String getName();

    String getWidgetType();

    String getThingId();

    String getControllerId();
}
