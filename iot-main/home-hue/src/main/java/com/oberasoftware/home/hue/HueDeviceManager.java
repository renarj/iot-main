package com.oberasoftware.home.hue;

import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.iot.core.model.OnOffValue;

import java.util.List;
import java.util.Optional;

public interface HueDeviceManager {
    List<Device> getDevices();

    Optional<Device> findDevice(String bridgeId, String lightId);

    List<HueDeviceState> retrieveStates(String bridgeId);

    boolean switchState(String bridgeId, String lightId, OnOffValue state);
}
