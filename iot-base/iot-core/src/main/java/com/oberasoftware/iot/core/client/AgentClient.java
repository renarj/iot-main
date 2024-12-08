package com.oberasoftware.iot.core.client;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.RuleItem;

import java.util.List;
import java.util.Optional;

public interface AgentClient extends ClientBase {
    IotThing createOrUpdate(IotThing thing) throws IOTException;

    Controller createOrUpdate(Controller controller) throws IOTException;

    boolean remove(String controllerId, String thingId) throws IOTException;

    List<Controller> getControllers() throws IOTException;

    Optional<Controller> getController(String controller) throws IOTException;

    Optional<IotThing> getThing(String controllerId, String thingId) throws IOTException;

    List<IotThing> getThings(String controllerId) throws IOTException;

    List<IotThing> getThings(String controllerId, String pluginId) throws IOTException;

    List<IotThing> getChildren(String controllerId, String thingId) throws IOTException;

    List<IotThing> getChildren(String controllerId, String thingId, String type) throws IOTException;

    List<IotThing> getThings(String controllerId, String pluginId, String type) throws IOTException;

    List<RuleItem> getRules(String controllerId) throws IOTException;
}
