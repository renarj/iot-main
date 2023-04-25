package com.oberasoftware.iot.train;

import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.TrainDAO;
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
            LOG.info("Ensuring an IotThing is created for locomotive: {}", loc);
            var optionalThing = thingClient.getThing(loc.getControllerId(), Integer.toString(loc.getLocAddress()));
            if(!optionalThing.isPresent()) {
                var thing = ThingBuilder.create(loc.getThingId(), loc.getControllerId())
                        .friendlyName(loc.getName())
                        .addAttributes("speed", "direction")
                        .addProperty("locAddress", Integer.toString(loc.getLocAddress()))
                        .addProperty("dccMode", loc.getStepMode().name())
                        .build();
                var persistedThing = thingClient.createOrUpdate(thing);
            }

            LOG.info("IOTThing entity created or updated, storing locomotive: {}", loc);
            centralDatastore.store(loc);
        } catch (IOTException e) {
            LOG.error("Could not store IotThing", e);
        }
    }

    @Override
    public void remove(Locomotive loc) {
        try {
            centralDatastore.delete(Locomotive.class, loc.getId());
        } catch (DataStoreException e) {
            LOG.error("Could not remove locomotive: " + loc.getId(), e);
        }
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
