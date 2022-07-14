package com.oberasoftware.iot.core.model;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface Controller extends IotBaseEntity {
    String getControllerId();

    List<IotThing> getDevices();
}
