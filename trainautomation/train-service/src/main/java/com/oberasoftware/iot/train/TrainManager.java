package com.oberasoftware.iot.train;

import com.oberasoftware.iot.core.train.model.Locomotive;

import java.util.List;
import java.util.Optional;

public interface TrainManager {
    void store(Locomotive loc);

    boolean remove(String controllerId, String thingId);

    List<Locomotive> findLocs(String controllerId);

    List<Locomotive> findAllLocs();

    Optional<Locomotive> findLoc(String controllerId, String locId);
}
