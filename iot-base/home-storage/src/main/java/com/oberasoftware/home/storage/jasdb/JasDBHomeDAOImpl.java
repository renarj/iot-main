package com.oberasoftware.home.storage.jasdb;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.iot.core.model.*;
import com.oberasoftware.iot.core.model.storage.*;
import com.oberasoftware.iot.core.model.storage.impl.*;
import com.oberasoftware.iot.core.storage.HomeDAO;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBHomeDAOImpl extends BaseDAO implements HomeDAO {
    private static final Logger LOG = getLogger(JasDBHomeDAOImpl.class);

    @Override
    public <T extends IotBaseEntity> Optional<T> findItem(Class<T> type, String id) {
        try {
            T result = getEntityManager().findEntity(type, id);
            return Optional.ofNullable(result);
        } catch(JasDBException e) {
            LOG.error("Unable to load item", e);
            return empty();
        }
    }

    @Override
    public Optional<Container> findContainer(String id) {
        try {
            ContainerImpl container = getEntityManager().findEntity(ContainerImpl.class, id);
            return Optional.of(container);
        } catch(JasDBException e) {
            LOG.error("Unable to load container", e);
            return empty();
        }
    }

    @Override
    public List<Container> findDashboardContainers(String dashboardId) {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("dashboardId", dashboardId).build()));
    }

    @Override
    public List<Container> findContainers() {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .build()));
    }

    @Override
    public List<Container> findContainers(String parentId) {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("parentContainerId", parentId).build()));
    }

    @Override
    public List<Dashboard> findDashboards() {
        return newArrayList(findItems(DashboardImpl.class, new ImmutableMap.Builder<String, String>()
                .build(), newArrayList("weight")));
    }

    @Override
    public List<Widget> findWidgets(String containerId) {
        return newArrayList(findItems(WidgetImpl.class, new ImmutableMap.Builder<String, String>()
                .put("containerId", containerId).build()));
    }

    @Override
    public Optional<Controller> findController(String controllerId) {
        ControllerImpl controllerItem = findItem(ControllerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId).build());
        return ofNullable(controllerItem);
    }

    @Override
    public List<Controller> findControllers() {
        return newArrayList(findItems(ControllerImpl.class, new ImmutableMap.Builder<String, String>().build()));
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String deviceId) {
        IotThingImpl deviceItem = findItem(IotThingImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .put("thingId", deviceId).build());
        return ofNullable(deviceItem);
    }

    @Override
    public List<IotThing> findThings(String controllerId, String pluginId, String type) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("pluginId", pluginId)
                .put("type", type)
                .build());
    }

    @Override
    public List<IotThing> findThingsofType(String controllerId, String type) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("type", type)
                .build());
    }

    @Override
    public List<IotThing> findThingsWithSchema(String controllerId, String schemaId) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("schemaId", schemaId)
                .build());
    }

    @Override
    public List<IotThing> findThingsWithSchema(String schemaId) {
        return newArrayList(findItems(IotThingImpl.class, new ImmutableMap.Builder<String, String>()
                .put("schemaId", schemaId)
                .build()));
    }

    @Override
    public List<IotThing> findThings(String controllerId, String pluginId) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("pluginId", pluginId)
                .build());
    }

    @Override
    public List<IotThing> findChildren(String controllerId, String parentId) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("parentId", parentId)
                .build());
    }

    @Override
    public List<IotThing> findChildren(String controllerId, String parentId, String type) {
        return findThings(controllerId, new ImmutableMap.Builder<String, String>()
                .put("parentId", parentId)
                .put("type", type)
                .build());
    }

    @Override
    public List<IotThing> findThings(String controllerId) {
        return findThings(controllerId, new HashMap<>());
    }

    @Override
    public List<IotThing> findThings(String controllerId, Map<String, String> queryParams) {
        return newArrayList(findItems(IotThingImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .putAll(queryParams)
                .build()));
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type) {
        return newArrayList(findItems(type, new HashMap<>()));
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type, String controllerId) {
        return newArrayList(findItems(type, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public List<RuleItem> findRules(String controllerId) {
        return newArrayList(findItems(RuleItemImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public List<RuleItem> findRules() {
        return newArrayList(findItems(RuleItemImpl.class, new HashMap<>()));
    }


    @Override
    public List<ThingSchema> findSchemas() {
        return newArrayList(findItems(ThingSchemaImpl.class, new HashMap<>()));
    }

    @Override
    public List<Plugin> findPlugins() {
        return newArrayList(findItems(PluginImpl.class, new HashMap<>()));
    }

    @Override
    public Optional<Plugin> findPlugin(String pluginId) {
        PluginImpl pluginItem = findItem(PluginImpl.class, new ImmutableMap.Builder<String, String>()
                .put("pluginId", pluginId)
                .build());
        return ofNullable(pluginItem);
    }

    @Override
    public List<ThingSchema> findSchemas(String pluginId) {
        return newArrayList(findItems(ThingSchemaImpl.class, new ImmutableMap.Builder<String, String>()
                .put("pluginId", pluginId)
                .build()));
    }

    @Override
    public Optional<ThingSchema> findSchema(String pluginId, String templateId) {
        ThingSchemaImpl templateItem = findItem(ThingSchemaImpl.class, new ImmutableMap.Builder<String, String>()
                .put("schemaId", templateId)
                .put("pluginId", pluginId).build());
        return ofNullable(templateItem);
    }
}
