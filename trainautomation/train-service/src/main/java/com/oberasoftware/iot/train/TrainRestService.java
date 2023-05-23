package com.oberasoftware.iot.train;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.train.model.Locomotive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainRestService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainRestService.class);

    @Autowired
    private TrainManager trainManager;

    @RequestMapping(value = "/locomotives", method = RequestMethod.POST)
    public ResponseEntity<Object> createLoc(@RequestBody Locomotive locomotive) throws IOTException {
        LOG.info("Stored locomotive: {}", locomotive);
        trainManager.store(locomotive);

        var optionalLoc = trainManager.findLoc(locomotive.getControllerId(), locomotive.getThingId());
        if(optionalLoc.isPresent()) {
            return new ResponseEntity<>(optionalLoc.get(), HttpStatus.OK);
        } else {
            LOG.error("Loc could not be created: {}", locomotive);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/controllers({controllerId})/locomotives({thingId})", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteLocomotive(@PathVariable String controllerId, @PathVariable String thingId) {
        LOG.info("Received locomotive removal request on controller: {} for loc: {}", controllerId, thingId);
        boolean result = trainManager.remove(controllerId, thingId);
        if(result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping("/locomotives")
    public List<Locomotive> getLocomotives() {
        LOG.info("Requesting all locomotives stored");
        return trainManager.findAllLocs();
    }

    @RequestMapping("/controllers({controllerId})/locomotives")
    public List<Locomotive> getLocomotives(@PathVariable String controllerId) {
        LOG.info("Requesting all locomotives for controller: {}", controllerId);
        return trainManager.findLocs(controllerId);
    }

    @RequestMapping("/controllers({controllerId})/locomotives({thingId})")
    public ResponseEntity<Object> getLocomotive(@PathVariable String controllerId, @PathVariable String thingId) {
        LOG.info("Requesting locomotive for controller: {} and thingId: {}", controllerId, thingId);
        var foundLoc = trainManager.findLoc(controllerId, thingId);
        return foundLoc.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
