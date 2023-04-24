package com.oberasoftware.iot.core.storage;

import com.oberasoftware.iot.core.train.model.Locomotive;

import java.util.List;
import java.util.Optional;

public interface TrainDAO {
    List<Locomotive> findLocs(String controllerId);

    List<Locomotive> findAllLocs();

    Optional<Locomotive> findLoc(String controllerId, String locId);
}
