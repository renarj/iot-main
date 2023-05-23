package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;

public interface LocThingRepository {
    void startSync();

    List<IotThing> getLocomotiveForLocAddress(String controllerId, int locAddress);
}
