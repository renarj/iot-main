package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface DeviceManager {
    IotThing registerThing(IotThing thing) throws IOTException;

    List<IotThing> getThings(String controllerId);

    Optional<IotThing> findThing(String controllerId, String deviceId);
}
