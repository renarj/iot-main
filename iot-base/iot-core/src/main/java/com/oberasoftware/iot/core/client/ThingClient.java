package com.oberasoftware.iot.core.client;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;
import java.util.Optional;

public interface ThingClient {
    void configure(String baseUrl, String apiToken);

    IotThing createOrUpdate(IotThing thing) throws IOTException;

    Controller createOrUpdate(Controller controller) throws IOTException;

    Optional<IotThing> getThing(String controllerId, String thingId) throws IOTException;

    List<IotThing> getThings(String controllerId) throws IOTException;
}
