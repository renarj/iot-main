package com.oberasoftware.home.data;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.oberasoftware.iot.activemq.RabbitMQTopicSender;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.ThingManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.AttributeType;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapToJson;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ThingManagerImpl implements ThingManager {
    private static final Logger LOG = getLogger(ThingManagerImpl.class);

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private RabbitMQTopicSender topicSender;

    @Value("${command.producer.topic:}")
    private String commandTopic;

    @Override
    public Controller createOrUpdateController(String controllerId, Map<String, String> properties) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<Controller> controllerItem = homeDAO.findController(controllerId);
            if (controllerItem.isEmpty()) {
                LOG.debug("Initial startup, new controller detected registering in central datastore");
                return centralDatastore.store(new ControllerImpl(generateId(), controllerId, "default", properties));
            } else {
                var controller = controllerItem.get();
                if(havePropertiesChanged(controller.getProperties(), properties)) {
                    LOG.debug("Controller: {} was already registered received new properties: {}", controllerId, properties);
                    return centralDatastore.store(new ControllerImpl(controller.getId(), controllerId, "default",
                            mergeProperties(controller.getProperties(), properties)));
                } else {
                    LOG.debug("Controller: {} was already registered and data is not updated", controllerId);
                    return controllerItem.get();
                }
            }
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    private Map<String, String> mergeProperties(Map<String, String> currentConfig, Map<String, String> updatedProperties) {
        if(currentConfig.isEmpty()) {
            return updatedProperties;
        } else {
            Map<String, String> mergedProperties = new HashMap<>(currentConfig);
            mergedProperties.putAll(updatedProperties);
            return mergedProperties;
        }
    }

    @Override
    public IotThing createOrUpdateThing(String controllerId, String thingId, IotThingImpl updatedThing) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<IotThing> thing = homeDAO.findThing(controllerId, thingId);
            IotThing result;
            if(thing.isPresent()) {
                IotThing item = thing.get();

                LOG.info("Thing: {} already exist, properties have changed, updating device with id: {}", thingId, item.getId());
                var mergedProperties = mergeProperties(item.getProperties(), updatedThing.getProperties());
                updatedThing.setId(item.getId());
                updatedThing.setProperties(mergedProperties);
                var attr = mergeAttributesWithSchema(item.getAttributes(), updatedThing);
                updatedThing.setAttributes(attr);

                mergeValues(item, updatedThing);
                ensureValid(updatedThing);
            } else {
                String id = generateId();
                LOG.debug("Device: {} does not yet exist, creating new with id: {}", thingId, id);
                updatedThing.setId(id);
                ensureValid(updatedThing);
                var attr = mergeAttributesWithSchema(Maps.newHashMap(), updatedThing);
                updatedThing.setAttributes(attr);
            }
            result = centralDatastore.store(updatedThing);

            var command = new BasicCommandImpl();
            command.setControllerId(controllerId);
            command.setThingId(thingId);
            command.setCommandType(CommandType.CONFIG_UPDATE);
            topicSender.publish(commandTopic, mapToJson(command));

            return result;
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    /**
     * This is to merge existing values into the updated entity
     * @param existing
     * @param updated
     */
    private void mergeValues(IotThing existing, IotThingImpl updated) {
        //ensure we keep the templateId/Schema if we have it, we should not unlink
        if(updated.getSchemaId() == null && existing.getSchemaId() != null) {
            updated.setSchemaId(existing.getSchemaId());
        }
        if(updated.getFriendlyName() == null && existing.getFriendlyName() != null) {
            updated.setFriendlyName(existing.getFriendlyName());
        }
        if(updated.getType() == null && existing.getType() != null) {
            updated.setType(existing.getType());
        }
        if(updated.getParentId() == null && existing.getParentId() != null) {
            updated.setParentId(existing.getParentId());
        }
    }

    private Map<String, AttributeType> mergeAttributesWithSchema(Map<String, AttributeType> existingAttributes, IotThingImpl updatedThing) {
        var pluginId = updatedThing.getPluginId();
        var schemaId = updatedThing.getSchemaId();
        Map<String, AttributeType> mergedMap = new HashMap<>(existingAttributes);
        if(updatedThing.getAttributes() != null && !updatedThing.getAttributes().isEmpty()) {
            mergedMap.putAll(updatedThing.getAttributes());
        }

        if(StringUtils.hasText(pluginId) && StringUtils.hasText(schemaId)) {
            var oSchema = homeDAO.findSchema(pluginId, schemaId);
            oSchema.ifPresent(ts -> {
                mergedMap.putAll(ts.getAttributes());
            });
        }
        return mergedMap;
    }

    private void ensureValid(IotThingImpl thing) throws IOTException {
        if(thing.getThingId() == null || thing.getThingId().isEmpty()) {
            throw new IOTException("Invalid Thing, missing thingId");
        }
        if(thing.getControllerId() == null || thing.getControllerId().isEmpty()) {
            throw new IOTException("Invalid ControllerId is specified");
        }
        if(thing.getPluginId() == null || thing.getPluginId().isEmpty()) {
            throw new IOTException("No PluginId defined for Thing");
        }
        if(homeDAO.findController(thing.getControllerId()).isEmpty()) {
            throw new IOTException("Controller: " + thing.getControllerId() + " does not exist");
        }
        validateSchema(thing);
    }

    private void validateSchema(IotThingImpl thing) throws IOTException {
        var schemaId = thing.getSchemaId();
        if(schemaId != null && !schemaId.equalsIgnoreCase("Plugin")) {
            //non-plugin can have any relation and need to be valid to a schema
            if(schemaId.isEmpty()) {
                throw new IOTException("No Schema specified for thing: " + thing.getThingId());
            }
            var oSchema = homeDAO.findSchema(thing.getPluginId(), schemaId)
                    .orElseThrow(() -> new IOTException("Invalid schema specified: " + schemaId + ", does not exist for thing: " + thing.getThingId()));
            validateParentRelations(thing.getControllerId(), thing.getParentId(), oSchema.getParentType());
        } else {
            //plugins always have a relation to controllers
            validateParentRelations(thing.getControllerId(), thing.getParentId(), "Controller");
        }
    }

    private void validateParentRelations(String controllerId, String parentId, String schemaParentType) throws IOTException {
        if(parentId == null) {
            throw new IOTException("No parent link was specified for thing");
        }
        switch(schemaParentType) {
            case "Controller":
                homeDAO.findController(parentId).orElseThrow(() -> new IOTException("Thing Parent Controller: " + parentId + " specified does not exist"));
                break;
            case "Plugin":
                var oPlugin = homeDAO.findThing(controllerId, parentId);
                if(oPlugin.isPresent()) {
                    if(!oPlugin.get().getType().equalsIgnoreCase("Plugin")) {
                        throw new IOTException("Parent link is not of required Plugin Type");
                    }
                }
                break;
            default:
                var oThing = homeDAO.findThing(controllerId, parentId);
                if(oThing.isPresent()) {
                    if(!oThing.get().getSchemaId().equalsIgnoreCase(schemaParentType)) {
                        throw new IOTException("Parent link is not of required Schema: " + schemaParentType);
                    }
                } else {
                    throw new IOTException("Specified parent link: " + parentId + " is not valid, could not be found");
                }
        }
    }

    @Override
    public IotThing installPluginOnController(String controllerId, String pluginId) throws IOTException {
        var oPlugin = homeDAO.findPlugin(pluginId);

        if(oPlugin.isPresent()) {
            var plugin = oPlugin.get();
            return createOrUpdateThing(controllerId, plugin.getPluginId(), ThingBuilder
                    .create(pluginId, controllerId)
                    .plugin(pluginId)
                    .type("Plugin")
                    .friendlyName(plugin.getFriendlyName())
                    .schema("Plugin")
                    .build());
        } else {
            throw new IOTException("Cannot install plugin, plugin does not exist on IoT system");
        }
    }

    @Override
    public boolean removeThing(String controllerId, String thingId) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<IotThing> thing = homeDAO.findThing(controllerId, thingId);
            if(thing.isPresent()) {
                if(!homeDAO.findChildren(controllerId, thingId).isEmpty()) {
                    LOG.debug("Deleted thing: {} on controller: {}", thingId, controllerId);
                    centralDatastore.delete(IotThingImpl.class, thing.get().getId());
                    return true;
                } else {
                    LOG.error("Could not delete Thing: {}, existing dependent Things present", thingId);
                    return false;
                }
            } else {
                LOG.warn("Tried to remove thing: {} on controller: {} but was not found", thingId, controllerId);
                return false;
            }
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public List<Controller> findControllers() {
        return homeDAO.findControllers();
    }

    @Override
    public Optional<Controller> findController(String controllerId) {
        return homeDAO.findController(controllerId);
    }

    @Override
    public List<IotThing> findThings(String controllerId) {
        return homeDAO.findThings(controllerId);
    }

    @Override
    public List<IotThing> findThings(String controlerId, String pluginId) {
        return homeDAO.findThings(controlerId, pluginId);
    }

    @Override
    public List<IotThing> findThings(String controlerId, String pluginId, String type) {
        return homeDAO.findThings(controlerId, pluginId, type);
    }

    @Override
    public List<IotThing> findThingsWithSchema(String controlerId, String schemaId) {
        return homeDAO.findThingsWithSchema(controlerId, schemaId);
    }

    @Override
    public List<IotThing> findThingsWithSchema(String schemaId) {
        return homeDAO.findThingsWithSchema(schemaId);
    }

    @Override
    public List<IotThing> findThingsWithType(String controllerId, String type) {
        return homeDAO.findThingsofType(controllerId, type);
    }

    @Override
    public List<IotThing> findChildren(String controllerId, String parentId) {
        return homeDAO.findChildren(controllerId, parentId);
    }

    @Override
    public List<IotThing> findChildren(String controllerId, String parentId, String type) {
        return homeDAO.findChildren(controllerId, parentId, type);
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String id) {
        return homeDAO.findThing(controllerId, id);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private boolean havePropertiesChanged(Map<String, String> previousProperties, Map<String, String> newProperties) {
        MapDifference<String, String> diff = Maps.difference(previousProperties, newProperties);

        return !diff.entriesOnlyOnRight().isEmpty();
    }
}
