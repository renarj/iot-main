package com.oberasoftware.iot.core.storage;

import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.ThingTemplate;
import com.oberasoftware.iot.core.model.storage.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author renarj
 */
public interface HomeDAO {
    <T extends IotBaseEntity> Optional<T> findItem(Class<T> type, String id);

    Optional<Controller> findController(String controllerId);

    List<Controller> findControllers();

    Optional<Container> findContainer(String id);

    List<Container> findDashboardContainers(String dashboardId);

    List<Container> findContainers();

    List<Container> findContainers(String parentId);

    List<Dashboard> findDashboards();

    List<Widget> findWidgets(String containerId);

    List<IotThing> findThings(String controllerId);

    Optional<IotThing> findThing(String controllerId, String thingId);

    List<IotThing> findThings(String controllerId, String pluginId);

    List<IotThing> findChildren(String controllerId, String parentId);

    List<IotThing> findThings(String controllerId, String pluginId, String type);

    List<IotThing> findThings(String controllerId, Map<String, String> queryParams);

    List<ThingTemplate> findTemplates();

    List<ThingTemplate> findTemplates(String pluginId);

    Optional<ThingTemplate> findTemplate(String pluginId, String templateId);

    <T extends VirtualItem> List<T> findVirtualItems(Class<T> type);

    <T extends VirtualItem> List<T> findVirtualItems(Class<T> type, String controllerId);

    List<RuleItem> findRules();

    List<RuleItem> findRules(String controllerId);
}
