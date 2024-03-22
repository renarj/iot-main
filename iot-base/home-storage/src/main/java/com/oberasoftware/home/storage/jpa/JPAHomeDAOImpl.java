package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.model.*;
import com.oberasoftware.iot.core.model.storage.*;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JPAHomeDAOImpl implements HomeDAO {

    @Autowired
    private ThingRepository thingRepository;

    @Autowired
    private ControllerRepository controllerRepository;

    @Autowired
    private PluginRepository pluginRepository;

    @Override
    public <T extends IotBaseEntity> Optional<T> findItem(Class<T> type, String id) {
        return Optional.empty();
    }

    @Override
    public Optional<Controller> findController(String controllerId) {
        return Optional.of(controllerRepository.findByControllerId(controllerId));
    }

    @Override
    public List<Controller> findControllers() {
        List<Controller> controllers = new ArrayList<>();
        controllerRepository.findAll().forEach(controllers::add);
        return controllers;
    }

    @Override
    public Optional<Container> findContainer(String id) {
        return Optional.empty();
    }

    @Override
    public List<Container> findDashboardContainers(String dashboardId) {
        return null;
    }

    @Override
    public List<Container> findContainers() {
        return null;
    }

    @Override
    public List<Container> findContainers(String parentId) {
        return null;
    }

    @Override
    public List<Dashboard> findDashboards() {
        return null;
    }

    @Override
    public List<Widget> findWidgets(String containerId) {
        return null;
    }

    @Override
    public List<IotThing> findThings(String controllerId) {
        return new ArrayList<>(thingRepository.findByControllerId(controllerId));
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String thingId) {
        return Optional.of(thingRepository.findByThingIdAndControllerId(thingId, controllerId));
    }

    @Override
    public List<IotThing> findThings(String controllerId, String pluginId) {
        return new ArrayList<>(thingRepository.findByControllerIdAndPluginId(controllerId, pluginId));
    }

    @Override
    public List<IotThing> findChildren(String controllerId, String parentId) {
        return new ArrayList<>(thingRepository.findByControllerIdAndParentId(controllerId, parentId));
    }

    @Override
    public List<IotThing> findThings(String controllerId, String pluginId, String type) {
        return new ArrayList<>(thingRepository.findByControllerIdAndPluginIdAndType(controllerId, pluginId, type));
    }

    @Override
    public List<IotThing> findThingsWithSchema(String controllerId, String schemaId) {
        return null;
    }

    @Override
    public List<IotThing> findThings(String controllerId, Map<String, String> queryParams) {
        return null;
    }

    @Override
    public List<ThingSchema> findSchemas() {
        return null;
    }

    @Override
    public List<Plugin> findPlugins() {
        List<Plugin> plugins = new ArrayList<>();
        pluginRepository.findAll().forEach(plugins::add);
        return plugins;
    }

    @Override
    public Optional<Plugin> findPlugin(String pluginId) {
        return Optional.of(pluginRepository.findByPluginId(pluginId));
    }

    @Override
    public List<ThingSchema> findSchemas(String pluginId) {
        return null;
    }

    @Override
    public Optional<ThingSchema> findSchema(String pluginId, String schemaId) {
        return Optional.empty();
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type) {
        return null;
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type, String controllerId) {
        return null;
    }

    @Override
    public List<RuleItem> findRules() {
        return null;
    }

    @Override
    public List<RuleItem> findRules(String controllerId) {
        return null;
    }
}
