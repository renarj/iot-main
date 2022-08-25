package com.oberasoftware.home.data;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ItemManagerImpl implements ItemManager {
    private static final Logger LOG = getLogger(ItemManagerImpl.class);

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private HomeDAO homeDAO;

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
    public IotThing createOrUpdateThing(String controllerId, String thingId, String friendlyName, String plugin, String parent, Map<String, String> properties) throws IOTException {
        centralDatastore.beginTransaction();
        try {
            Optional<IotThing> thing = homeDAO.findThing(controllerId, thingId);
            if(thing.isPresent()) {
                IotThing item = thing.get();

                if(havePropertiesChanged(item.getProperties(), properties) || (friendlyName != null && !friendlyName.equalsIgnoreCase(item.getFriendlyName()))) {
                    LOG.info("Thing: {} already exist, properties have changed, updating device with id: {}", thingId, item.getId());
                    var mergedProperties = mergeProperties(item.getProperties(), properties);

                    var t = new IotThingImpl(item.getId(), controllerId, thingId, friendlyName, plugin, parent, mergedProperties);
                    return centralDatastore.store(t);
                } else {
                    LOG.debug("Device: {} has not changed, not updating item: {}", thingId, item.getId());
                    return item;
                }
            } else {
                String id = generateId();
                LOG.debug("Device: {} does not yet exist, creating new with id: {}", thingId, id);
                var t = new IotThingImpl(id, controllerId, thingId, friendlyName, plugin, parent, properties);
                return centralDatastore.store(t);
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
    public List<IotThing> findThings(String controllerId) {
        return homeDAO.findThings(controllerId);
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
