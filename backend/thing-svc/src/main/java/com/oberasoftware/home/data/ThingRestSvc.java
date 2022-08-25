package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@RestController
@RequestMapping("/api")
public class ThingRestSvc {
    private static final Logger LOG = getLogger(ThingRestSvc.class);

    private static final String ERROR_FORMAT = "{\"error\":\"%s\"}";

    @Autowired
    private ItemManager itemManager;

    @RequestMapping(value = "/controllers({controllerId})/things", method = RequestMethod.GET)
    public List<IotThing> getThings(@PathVariable String controllerId) {
        LOG.debug("Requested list of all devices");

        return itemManager.findThings(controllerId);
    }

    @RequestMapping("/controllers")
    public List<Controller> getControllers() {
        LOG.debug("Requested a list of all controllers");
        return itemManager.findControllers();
    }

    @RequestMapping("/controllers({controllerId})/things({thingId})")
    public ResponseEntity<Object> getThing(@PathVariable String controllerId, @PathVariable String thingId) {
        LOG.info("Doing lookup for Thing: {} on controller: {}", thingId, controllerId);
        var item = itemManager.findThing(controllerId, thingId);
        return item.<ResponseEntity<Object>>map(iotThing -> new ResponseEntity<>(iotThing, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(String.format(ERROR_FORMAT, "Could not find Thing"), HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/controllers({controllerId})", method = RequestMethod.POST)
    public ResponseEntity<Object> createController(@PathVariable String controllerId, @RequestBody ControllerImpl controller) throws IOTException {
        if(StringUtils.hasText(controllerId) && controllerId.equalsIgnoreCase(controller.getControllerId())) {
            LOG.info("Creating or updating controller: {} data", controllerId);
            return new ResponseEntity<>(itemManager.createOrUpdateController(controllerId, controller.getProperties()), HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity controllerId: {} not matching API controllerId: {}", controller.getControllerId(), controllerId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/things", method = RequestMethod.POST)
    public ResponseEntity<Object> createThing(@PathVariable String controllerId, @RequestBody IotThingImpl thing) throws IOTException {
        if(StringUtils.hasText(controllerId) && controllerId.equalsIgnoreCase(thing.getControllerId()) && validateThing(thing)) {
            LOG.info("Creating thing: {} on controller: {}", thing.getThingId(), controllerId);
            var createdThing = itemManager.createOrUpdateThing(controllerId, thing.getThingId(), thing.getFriendlyName(),
                    thing.getPluginId(), thing.getParentId(), thing.getProperties());

            return new ResponseEntity<>(createdThing, HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity controllerId: {} not matching API controllerId: {}", thing.getControllerId(), controllerId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean validateThing(IotThing thing) {
        return StringUtils.hasText(thing.getFriendlyName()) &&
                StringUtils.hasText(thing.getThingId()) &&
                StringUtils.hasText(thing.getControllerId())
                && thing.getProperties() != null;

    }
}
