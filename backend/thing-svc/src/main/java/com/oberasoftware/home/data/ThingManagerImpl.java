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
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    public IotThing createOrUpdateThing(String controllerId, String thingId, String friendlyName, String plugin, String type, String parent, Map<String, String> properties, Set<String> attributes) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<IotThing> thing = homeDAO.findThing(controllerId, thingId);
            IotThing result = null;
            if(thing.isPresent()) {
                IotThing item = thing.get();

                LOG.info("Thing: {} already exist, properties have changed, updating device with id: {}", thingId, item.getId());
                var mergedProperties = mergeProperties(item.getProperties(), properties);

                var t = new IotThingImpl(item.getId(), controllerId, thingId, friendlyName, plugin, parent, mergedProperties);
                t.setAttributes(attributes);
                t.setType(type);
                t = ensureValid(t);

                result = centralDatastore.store(t);
            } else {
                String id = generateId();
                LOG.debug("Device: {} does not yet exist, creating new with id: {}", thingId, id);
                var t = new IotThingImpl(id, controllerId, thingId, friendlyName, plugin, parent, properties);
                t.setType(type);
                t.setAttributes(attributes);
                t = ensureValid(t);
                result = centralDatastore.store(t);
            }

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

    private IotThingImpl ensureValid(IotThingImpl thing) throws IOTException {
        if(thing.getThingId() == null || thing.getThingId().isEmpty()) {
            throw new IOTException("Invalid Thing, missing thingId");
        }
        if(thing.getControllerId() == null || thing.getControllerId().isEmpty()) {
            throw new IOTException("Invalid ControllerId is specified");
        }
        if(homeDAO.findController(thing.getControllerId()).isEmpty()) {
            throw new IOTException("Controller: " + thing.getControllerId() + " does not exist");
        }
        return ensureLinked(thing);
    }

    private IotThingImpl ensureLinked(IotThingImpl thing) throws IOTException {
        if(thing.getParentId() == null) {
            throw new IOTException("No parent link was specified for thing: " + thing.getThingId());
        }
        if(!thing.getParentId().equalsIgnoreCase(thing.getControllerId())) {
            if(homeDAO.findThing(thing.getControllerId(), thing.getParentId()).isEmpty()) {
                throw new IOTException("Specified parent link: " + thing.getParentId() + " is not valid");
            }
        }
        return thing;
    }

    @Override
    public boolean removeThing(String controllerId, String thingId) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<IotThing> thing = homeDAO.findThing(controllerId, thingId);
            if(thing.isPresent()) {
                LOG.debug("Deleted thing: {} on controller: {}", thingId, controllerId);
                centralDatastore.delete(IotThingImpl.class, thing.get().getId());
                return true;
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
    public List<IotThing> findChildren(String controllerId, String parentId) {
        return homeDAO.findChildren(controllerId, parentId);
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
