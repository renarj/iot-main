package com.oberasoftware.iot.core.client;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.states.State;

import java.util.Optional;

public interface StateClient {
    void configure(String baseUrl, String apiToken);

    Optional<State> getState(String controllerId, String thingId) throws IOTException;
}
