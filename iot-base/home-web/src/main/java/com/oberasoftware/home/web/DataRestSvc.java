package com.oberasoftware.home.web;

import com.oberasoftware.iot.core.client.StateClient;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.State;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/data")
public class DataRestSvc {
    private static final Logger LOG = getLogger(DataRestSvc.class);

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private StateClient stateClient;

    @RequestMapping("/controllers")
    public List<Controller> getControllers() throws IOTException {
        LOG.debug("Requested a list of all controllers");
        return thingClient.getControllers();
    }

    @RequestMapping(value = "/controllers({controllerId})/things", method = RequestMethod.GET)
    public List<IotThing> getThings(@PathVariable String controllerId) throws IOTException {
        LOG.debug("Requested list of all things for controller: {}", controllerId);

        return thingClient.getThings(controllerId);
    }

    @RequestMapping(value = "/controllers({controllerId})/things({thingId})", method = RequestMethod.GET)
    public ResponseEntity<Object> getThing(@PathVariable String controllerId, @PathVariable String thingId) throws IOTException {
        LOG.debug("Requested thing: {} on controller: {}", thingId, controllerId);

        var thing = thingClient.getThing(controllerId, thingId);

        return thing.<ResponseEntity<Object>>map(iotThing ->
                new ResponseEntity<>(iotThing, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/controllers({controllerId})/things({thingId})/state", method = RequestMethod.GET)
    public ResponseEntity<Object> getState(@PathVariable String controllerId, @PathVariable String thingId) throws IOTException {
        LOG.debug("Requesting state of thing: {} on controller: {}", thingId, controllerId);

        Optional<State> state = stateClient.getState(controllerId, thingId);
        return state.<ResponseEntity<Object>>map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}