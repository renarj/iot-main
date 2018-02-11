package com.oberasoftware.max.web.api.managers;


import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.MutableItem;
import com.oberasoftware.max.web.api.model.Widget;

import java.util.List;

/**
 * @author renarj
 */
public interface UIManager {
    List<Container> getDashboardContainers(String dashboardId);

    List<Container> getAllContainers();

    List<Container> getChildren(String containerId);

    Container getContainer(String containerId);

    List<Widget> getItems(String containerId);

    void setWidgetProperty(String itemId, String property, String value);

    void setContainerProperty(String itemId, String property, String value);

    void setParentContainer(String itemId, String parentContainerId);

    void deleteContainer(String containerId);

    void deleteWidget(String itemId);

    <T extends MutableItem> T store(T item);
}
