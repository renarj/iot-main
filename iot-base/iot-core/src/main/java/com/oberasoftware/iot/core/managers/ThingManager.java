package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author renarj
 */
public interface ThingManager {
    Controller createOrUpdateController(String controllerId, Map<String, String> properties) throws IOTException;

    IotThing createOrUpdateThing(String controllerId, String thingId, IotThingImpl thing) throws IOTException;

    IotThing installPluginOnController(String controllerId, String pluginId) throws IOTException;

    boolean removeThing(String controllerId, String thingId) throws IOTException;

    boolean removeController(String controllerId) throws IOTException;

    List<Controller> findControllers();

    Optional<Controller> findController(String controllerId);

    List<IotThing> findThings(String controllerId);

    List<IotThing> findThings(String controlerId, String pluginId);

    List<IotThing> findThings(String controlerId, String pluginId, String type);

    List<IotThing> findLinkedItems(String controllerId, String thingId, String type);

    List<IotThing> findLinkedItems(String controllerId, String thingId);

    List<IotThing> findThingsWithSchema(String controlerId, String schemaId);

    List<IotThing> findThingsWithSchema(String schemaId);

    List<IotThing> findThingsWithType(String controllerId, String type);

    List<IotThing> findChildren(String controllerId, String parentId);

    List<IotThing> findChildren(String controllerId, String parentId, String type);

    Optional<IotThing> findThing(String controllerId, String id);
}
