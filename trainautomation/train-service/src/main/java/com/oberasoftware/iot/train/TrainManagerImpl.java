package com.oberasoftware.iot.train;

import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.TrainDAO;
import com.oberasoftware.iot.core.train.TrainConstants;
import com.oberasoftware.iot.core.train.model.Locomotive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainManagerImpl implements TrainManager {
    private static final Logger LOG = LoggerFactory.getLogger(TrainManagerImpl.class);

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private TrainDAO trainDAO;

    @Autowired
    private CentralDatastore centralDatastore;

    @Override
    public void store(Locomotive loc) {
        try {
            var oController = thingClient.getController(loc.getControllerId());
            if(oController.isPresent()) {

                LOG.info("Ensuring an IotThing is created for locomotive: {}", loc);
                var optionalThing = thingClient.getThing(loc.getControllerId(), loc.getThingId());
                if (optionalThing.isEmpty()) {
                    var thing = ThingBuilder.create(loc.getThingId(), loc.getControllerId())
                            .friendlyName(loc.getName())
                            .type(Locomotive.LOCOMOTIVE_TYPE)
                            .plugin(TrainConstants.EXTENSION_ID)
                            .parent(loc.getCommandStation())
                            .addAttributes(Locomotive.SPEED_ATTR, Locomotive.DIRECTION_ATTR)
                            .addProperty(Locomotive.LOC_ADDRESS, Integer.toString(loc.getLocAddress()))
                            .addProperty(Locomotive.DCC_MODE, loc.getStepMode().name())
                            .build();
                    thingClient.createOrUpdate(thing);
                }


                var oLoc = trainDAO.findLoc(loc.getControllerId(), loc.getThingId());
                if(oLoc.isPresent()) {
                    LOG.info("Pre-existing loc, ensuring updating existing loc entity: {}", oLoc);
                    String id = oLoc.get().getId();
                    loc.setId(id);
                }
                LOG.info("IOTThing entity created or updated, storing locomotive: {}", loc);
                centralDatastore.store(loc);
            } else {
                LOG.warn("Could not store locomotive: {}, controller: {} does not exist", loc.getThingId(), loc.getControllerId());
            }
        } catch (IOTException e) {
            LOG.error("Could not store IotThing", e);
        }
    }

    @Override
    public boolean remove(String controllerId, String thingId) {
        var oLoc = findLoc(controllerId, thingId);
        oLoc.ifPresent(l -> {
            try {
                boolean result = thingClient.remove(controllerId, thingId);
                if(result) {
                    centralDatastore.delete(Locomotive.class, l.getId());
                    LOG.info("Removed thing and locomotive: {} on controller: {}", thingId, controllerId);
                } else {
                    LOG.warn("Could not delete remove thing: {} on controller: {}", thingId, controllerId);
                }
            } catch (IOTException e) {
                LOG.error("Could not remove locomotive: " + l.getId(), e);
            }
        });

        return oLoc.isPresent();
    }

    @Override
    public List<Locomotive> findLocs(String controllerId) {
        return trainDAO.findLocs(controllerId);
    }

    @Override
    public List<Locomotive> findAllLocs() {
        return trainDAO.findAllLocs();
    }

    @Override
    public Optional<Locomotive> findLoc(String controllerId, String locId) {
        return trainDAO.findLoc(controllerId, locId);
    }
}
