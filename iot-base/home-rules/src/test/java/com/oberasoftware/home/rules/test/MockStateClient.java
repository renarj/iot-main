package com.oberasoftware.home.rules.test;

import com.oberasoftware.iot.core.client.StateClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MockStateClient implements StateClient {
    @Autowired
    public StateManager stateManager;

    @Override
    public void configure(String baseUrl, String apiToken) {

    }

    @Override
    public Optional<State> getState(String controllerId, String thingId) throws IOTException {
        return stateManager.getState(controllerId, thingId);
    }
}
