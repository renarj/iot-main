package com.oberasoftware.max.web.api.model;

/**
 * @author Renze de Vries
 */
public interface Widget extends MutableItem {
    long getWeight();

    String getContainerId();

    String getName();

    String getWidgetType();
}
