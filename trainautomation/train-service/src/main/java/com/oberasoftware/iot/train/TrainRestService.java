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
    public ResponseEntity<Object> createController(@RequestBody Locomotive locomotive) throws IOTException {
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

    @RequestMapping("/locomotives")
    public List<Locomotive> getLocomotives() {
        LOG.info("Requesting all locomotives stored");
        return trainManager.findAllLocs();
    }

    @RequestMapping("/locomotives({controllerId})")
    public List<Locomotive> getLocomotives(@PathVariable String controllerId) {
        LOG.info("Requesting all locomotives for controller: {}", controllerId);
        return trainManager.findLocs(controllerId);
    }
}
