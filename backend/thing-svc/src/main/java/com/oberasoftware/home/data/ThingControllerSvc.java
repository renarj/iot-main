package com.oberasoftware.home.data;

import com.oberasoftware.home.rest.model.RestItemDevice;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@RestController
@RequestMapping("/data")
public class ThingControllerSvc {
    private static final Logger LOG = getLogger(ThingControllerSvc.class);

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private StateManager stateManager;

    @RequestMapping("/controllers({controllerId})/devices")
    public List<RestItemDevice> getThings(@PathVariable String controllerId) {
        LOG.debug("Requested list of all devices");

        List<IotThing> deviceItems = itemManager.findThings(controllerId);
        return deviceItems.stream()
                .map(d -> new RestItemDevice(d, stateManager.getState(d.getId())))
                .collect(Collectors.toList());
    }

    @RequestMapping("/controllers")
    public List<Controller> getControllers() {
        LOG.debug("Requested a list of all controllers");
        return itemManager.findControllers();
    }

    @RequestMapping("/controllers({controllerId}/devices({thingId})")
    public IotThing getThing(@PathVariable String controllerId, @PathVariable String thingId) {
        return itemManager.findThing(controllerId, thingId).get();
    }

    @RequestMapping(value = "/controllers({controllerId}", method = RequestMethod.POST)
    public ResponseEntity<Object> createThing(@PathVariable String controllerId, @RequestBody IotThingImpl thing) throws IOTException {
        if(StringUtils.hasText(controllerId) && controllerId.equalsIgnoreCase(thing.getControllerId())) {
            LOG.info("Creating thing: {} on controller: {}", thing.getThingId(), controllerId);
            var createdThing = itemManager.createOrUpdateThing(controllerId, thing.getThingId(), thing.getPluginId(), thing.getParentId(), thing.getProperties());

            return new ResponseEntity<>(createdThing, HttpStatus.OK);
        } else {
            LOG.warn("Invalid entity controllerId: {} not matching API controllerId: {}", thing.getControllerId(), controllerId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
