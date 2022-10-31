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
public interface ItemManager {
    Controller createOrUpdateController(String controllerId, Map<String, String> properties) throws IOTException;

    IotThing createOrUpdateThing(String controllerId, String thingId, String friendlyName, String plugin, String parent, Map<String, String> properties, Set<String> attribute) throws IOTException;

    List<Controller> findControllers();

    List<IotThing> findThings(String controllerId);

    Optional<IotThing> findThing(String controllerId, String id);
}
