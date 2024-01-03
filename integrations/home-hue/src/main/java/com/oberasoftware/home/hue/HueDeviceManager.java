package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.legacymodel.OnOffValue;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;
import java.util.Optional;

public interface HueDeviceManager {
    List<IotThing> getDevices(String bridgeId);

    Optional<IotThing> findDevice(String bridgeId, String lightId);

    List<HueDeviceState> retrieveStates(String bridgeId);

    boolean switchState(String bridgeId, String lightId, OnOffValue state);
}
