package com.oberasoftware.iot.core.model.storage;


import com.oberasoftware.iot.core.model.IotBaseEntity;

public interface Dashboard extends IotBaseEntity {
    String getName();

    long getWeight();
}
