package com.oberasoftware.home.core.state;

import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class StateRestSvc {

    @Autowired
    private StateManager stateManager;

    @RequestMapping(value = "/controllers({controllerId})/things({thingId})", method = RequestMethod.GET)
    public ResponseEntity<State> getState(@PathVariable String controllerId, @PathVariable String thingId) {
        var oState = stateManager.getState(controllerId, thingId);
        return oState.map(state -> new ResponseEntity<>(state, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/controllers({controllerId})", method = RequestMethod.GET)
    public Map<String, State> getState(@PathVariable String controllerId) {
        return stateManager.getStates(controllerId);
    }

}
