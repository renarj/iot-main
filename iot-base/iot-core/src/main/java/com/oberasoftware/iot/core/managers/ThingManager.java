package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author renarj
 */
public interface ThingManager {
    Controller createOrUpdateController(String controllerId, Map<String, String> properties) throws IOTException;

    IotThing createOrUpdateThing(String controllerId, String thingId, String friendlyName, String plugin, String type, String parent, Map<String, String> properties, Set<String> attribute) throws IOTException;

    boolean removeThing(String controllerId, String thingId) throws IOTException;

    List<Controller> findControllers();

    Optional<Controller> findController(String controllerId);

    List<IotThing> findThings(String controllerId);

    List<IotThing> findThings(String controlerId, String pluginId);

    List<IotThing> findThings(String controlerId, String pluginId, String type);

    List<IotThing> findChildren(String controllerId, String parentId);

    Optional<IotThing> findThing(String controllerId, String id);
}
