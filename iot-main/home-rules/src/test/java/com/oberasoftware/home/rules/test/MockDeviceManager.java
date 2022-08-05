package com.oberasoftware.home.rules.test;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.DeviceManager;
import com.oberasoftware.iot.core.model.IotThing;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Renze de Vries
 */
@Component
public class MockDeviceManager implements DeviceManager {

    private List<IotThing> deviceItems = new CopyOnWriteArrayList<>();

    @Override
    public IotThing registerThing(IotThing thing) throws IOTException {
        return null;
    }

    public void addDevice(IotThing deviceItem) {
        this.deviceItems.add(deviceItem);
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String itemId) {
        return deviceItems.stream().filter(d -> d.getId().equals(itemId)).findFirst();
    }

    @Override
    public List<IotThing> getThings(String controllerId) {
        return new ArrayList<>(deviceItems);
    }
}
