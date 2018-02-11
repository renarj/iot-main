package com.oberasoftware.max.web.api.model;

/**
 * @author Renze de Vries
 */
public interface Container extends MutableItem {
    String getParentContainerId();

    String getDashboardId();

    String getName();
}
