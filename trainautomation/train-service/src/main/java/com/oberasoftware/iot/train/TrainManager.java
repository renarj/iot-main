package com.oberasoftware.iot.train;

import com.oberasoftware.iot.core.train.model.Locomotive;

import java.util.List;
import java.util.Optional;

public interface TrainManager {
    void store(Locomotive loc);

    void remove(Locomotive loc);

    List<Locomotive> findLocs(String controllerId);

    List<Locomotive> findAllLocs();

    Optional<Locomotive> findLoc(String controllerId, String locId);
}
