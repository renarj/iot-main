package com.oberasoftware.max.web.api.storage;


import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.Dashboard;
import com.oberasoftware.max.web.api.model.HomeEntity;
import com.oberasoftware.max.web.api.model.Widget;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface CentralDataDAO {
    <T extends HomeEntity> Optional<T> findItem(Class<T> type, String id);

    Optional<Container> findContainer(String id);

    List<Container> findDashboardContainers(String dashboardId);

    List<Container> findContainers();

    List<Container> findContainers(String parentId);

    List<Dashboard> findDashboards();

    List<Widget> findWidgets(String containerId);
}
