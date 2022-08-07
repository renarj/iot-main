package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author renarj
 */
public interface ItemManager {
    Controller createOrUpdateController(String controllerId) throws IOTException;

    IotThing createOrUpdateThing(String controllerId, String thingId, String plugin, String parent, Map<String, String> properties) throws IOTException;

    List<Controller> findControllers();

    List<IotThing> findThings(String controllerId);

    Optional<IotThing> findThing(String controllerId, String id);
}
