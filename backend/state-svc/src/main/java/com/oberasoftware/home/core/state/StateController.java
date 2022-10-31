package com.oberasoftware.home.core.state;

import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Renze de Vries
 */
@RestController
@RequestMapping("/api/state")
public class StateController {

    @Autowired
    private StateManager stateManager;

    @RequestMapping(value = "/({controllerId})/({thingId})", method = RequestMethod.GET)
    public State getState(@PathVariable String controllerId, @PathVariable String thingId) {
        return stateManager.getState(controllerId, thingId);
    }

    @RequestMapping(value = "/({controllerId})", method = RequestMethod.GET)
    public Map<String, State> getState(@PathVariable String controllerId) {
        return stateManager.getStates(controllerId);
    }

}
