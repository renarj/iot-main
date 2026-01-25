package com.oberasoftware.robodog.odrive.device;

import com.oberasoftware.robodog.odrive.ODriveInterface;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ODriveManager {
    private static final int CONTROL_MODE_VELOCITY = 2;
    private static final int INPUT_MODE_PASSTHROUGH = 1;

    private final ODriveInterface can;
    private final ODriveStateManager stateManager;
    private final Map<Integer, ODriveDevice> devices = new ConcurrentHashMap<>();

    public ODriveManager(ODriveInterface can, ODriveStateManager stateManager) {
        this.can = can;
        this.stateManager = stateManager;
    }

    public ODriveDevice createOrGet(int nodeId) {
        if(devices.containsKey(nodeId)) {
            return devices.get(nodeId);
        } else {
            return register(nodeId, "Node-" + nodeId);
        }
    }

    public ODriveDevice get(int nodeId) {
        return devices.get(nodeId);
    }

    public Collection<ODriveDevice> getAll() {
        return Collections.unmodifiableCollection(devices.values());
    }

    public ODriveDevice register(int nodeId, String name) {
        ODriveDevice d = new ODriveDevice(nodeId, name, can, stateManager);
        devices.put(nodeId, d);
        return d;
    }
}
