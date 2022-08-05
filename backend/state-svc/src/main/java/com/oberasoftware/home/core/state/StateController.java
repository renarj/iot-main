package com.oberasoftware.home.core.state;

import com.oberasoftware.robo.api.managers.StateManager;
import com.oberasoftware.robo.api.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Renze de Vries
 */
@RestController
@RequestMapping("/state")
public class StateController {

    @Autowired
    private StateManager stateManager;

    @RequestMapping("/{controllerId}/{itemId}")
    public State getState(@PathVariable String controllerId, @PathVariable String itemId) {
        return stateManager.getState(controllerId, itemId);
    }

    @RequestMapping("/{controllerId}")
    public List<State> getState(@PathVariable String controllerId) {
        return stateManager.getStates(controllerId);
    }

}
