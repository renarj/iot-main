package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.ThingManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    private ThingManager thingManager;

    @RequestMapping(value = "/controllers({controllerId})/things", method = RequestMethod.GET)
    public List<IotThing> getThings(@PathVariable String controllerId) {
        LOG.debug("Requested list of all things on controller: {}", controllerId);

        return thingManager.findThings(controllerId);
    }

    @RequestMapping("/controllers")
    public List<Controller> getControllers() {
        LOG.debug("Requested a list of all controllers");
        return thingManager.findControllers();
    }

    @RequestMapping("/controllers({controllerId})")
    public ResponseEntity<Controller> getController(@PathVariable String controllerId) {
        LOG.info("Requesting controller: {}", controllerId);
        var oController = thingManager.findController(controllerId);
        return oController.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/controllers({controllerId})/plugins({pluginId})/things", method = RequestMethod.GET)
    public List<IotThing> getThings(@PathVariable String controllerId, @PathVariable String pluginId, @RequestParam Optional<String> type) {
        LOG.debug("Requested list of all things on controller: {} and plugin: {} and type: {}", controllerId, pluginId, type);

        if(type.isPresent()) {
            return thingManager.findThings(controllerId, pluginId, type.get());
        } else {
            return thingManager.findThings(controllerId, pluginId);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/plugins", method = RequestMethod.GET)
    public List<IotThing> getPlugins(@PathVariable String controllerId) {
        LOG.debug("Requested list of all Plugins installed on controller: {}", controllerId);

        return thingManager.findThingsWithType(controllerId, "Plugin");
    }

    @RequestMapping(value = "/controllers({controllerId})/schemas({schemaId})/things", method = RequestMethod.GET)
    public List<IotThing> getControllerThingsWithSchema(@PathVariable String controllerId, @PathVariable String schemaId) {
        LOG.debug("Requested list of all things on controller: {} with Schema: {}", controllerId, schemaId);

        return thingManager.findThingsWithSchema(controllerId, schemaId);
    }

    @RequestMapping(value = "/schemas({schemaId})/things", method = RequestMethod.GET)
    public List<IotThing> getAllThingsWithSchema(@PathVariable String schemaId) {
        LOG.debug("Requested list of all things with Schema: {}", schemaId);

        return thingManager.findThingsWithSchema(schemaId);
    }

    @RequestMapping(value = "/controllers({controllerId})/things({thingId})/children", method = RequestMethod.GET)
    public List<IotThing> getChildren(@PathVariable String controllerId, @PathVariable String thingId, @RequestParam Optional<String> type) {
        LOG.debug("Requested list of all children on controller: {} of thing: {}", controllerId, thingId);

        if(type.isPresent()) {
            return thingManager.findChildren(controllerId, thingId, type.get());
        } else {
            return thingManager.findChildren(controllerId, thingId);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/children", method = RequestMethod.GET)
    public List<IotThing> getChildren(@PathVariable String controllerId) {
        LOG.debug("Requested list of all children on controller: {}", controllerId);

        return thingManager.findChildren(controllerId, controllerId);
    }

    @RequestMapping("/controllers({controllerId})/things({thingId})")
    public ResponseEntity<Object> getThing(@PathVariable String controllerId, @PathVariable String thingId) {
        LOG.info("Doing lookup for Thing: {} on controller: {}", thingId, controllerId);
        var item = thingManager.findThing(controllerId, thingId);
        return item.<ResponseEntity<Object>>map(iotThing -> new ResponseEntity<>(iotThing, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(String.format(ERROR_FORMAT, "Could not find Thing"), HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/controllers({controllerId})", method = RequestMethod.POST)
    public ResponseEntity<Object> createController(@PathVariable String controllerId, @RequestBody ControllerImpl controller) throws IOTException {
        if(StringUtils.hasText(controllerId) && controllerId.equalsIgnoreCase(controller.getControllerId())) {
            LOG.info("Creating or updating controller: {} data", controllerId);
            return new ResponseEntity<>(thingManager.createOrUpdateController(controllerId, controller.getProperties()), HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity controllerId: '{}' not matching API controllerId: '{}'", controller.getControllerId(), controllerId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/plugins", method = RequestMethod.POST)
    public ResponseEntity<Object> installPlugin(@PathVariable String controllerId, @RequestBody PluginImpl plugin) throws IOTException {
        if(StringUtils.hasText(controllerId) && StringUtils.hasText(plugin.getPluginId())) {
            LOG.info("Installing or updating plugin: {} on controller: {}", plugin, controllerId);
            return new ResponseEntity<>(thingManager.installPluginOnController(controllerId, plugin.getPluginId()), HttpStatus.CREATED);
        } else {
            LOG.warn("Invalid entity controllerId: '{}' or pluginId: '{}'", controllerId, plugin.getPluginId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/things", method = RequestMethod.POST)
    public ResponseEntity<Object> createThing(@PathVariable String controllerId, @RequestBody IotThingImpl thing) {
        if(StringUtils.hasText(controllerId) && controllerId.equalsIgnoreCase(thing.getControllerId()) && validateThing(thing)) {
            LOG.info("Creating thing: '{}' on controller: '{}'", thing.getThingId(), controllerId);
            LOG.info("Thing: {}", thing);
            try {
                var createdThing = thingManager.createOrUpdateThing(controllerId, thing.getThingId(), thing);

                return new ResponseEntity<>(createdThing, HttpStatus.CREATED);
            } catch(IOTException e) {
                return new ResponseEntity<>("{\"errorReason\":\"Invalid Thing provided\", \"detail\":\"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
            }
        } else {
            LOG.warn("Invalid entity controllerId: '{}' not matching API controllerId: '{}' or missing attributes", thing.getControllerId(), controllerId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/things({thingId})", method = RequestMethod.DELETE)
    public ResponseEntity<Object> removeThing(@PathVariable String controllerId, @PathVariable String thingId) throws IOTException {
        LOG.info("Received remove thing request for controller: {} and thing: {}", controllerId, thingId);
        boolean result = thingManager.removeThing(controllerId, thingId);
        if(result) {
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean validateThing(IotThing thing) {
        return StringUtils.hasText(thing.getFriendlyName()) &&
                StringUtils.hasText(thing.getThingId()) &&
                StringUtils.hasText(thing.getControllerId())
                && thing.getProperties() != null;

    }
}
