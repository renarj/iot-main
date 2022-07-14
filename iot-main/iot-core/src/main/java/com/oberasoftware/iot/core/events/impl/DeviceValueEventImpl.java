package com.oberasoftware.iot.core.events.impl;

import com.oberasoftware.iot.core.events.DeviceValueEvent;
import com.oberasoftware.iot.core.model.Value;

/**
 * @author renarj
 */
public class DeviceValueEventImpl implements DeviceValueEvent {
    private final String controllerId;
    private final String deviceId;
    private final Value value;
    private final String label;

    public DeviceValueEventImpl(String controllerId, String deviceId, Value value, String label) {
        this.controllerId = controllerId;
        this.deviceId = deviceId;
        this.value = value;
        this.label = label;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "DeviceValueEventImpl{" +
                "controllerId='" + controllerId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", value=" + value +
                ", label='" + label + '\'' +
                '}';
    }
}
