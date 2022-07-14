package com.oberasoftware.robo.core.model;

import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;

/**
 * @author Renze de Vries
 */
public class ControllerImpl implements Controller {
    private String controllerId;
    private long lastSeen;
    private List<IotThing> devices;

    public ControllerImpl(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public List<IotThing> getDevices() {
        return devices;
    }

    public void setDevices(List<IotThing> devices) {
        this.devices = devices;
    }

    @Override
    public String getId() {
        return controllerId;
    }

    @Override
    public String toString() {
        return "ControllerImpl{" +
                "controllerId='" + controllerId + '\'' +
                '}';
    }
}
