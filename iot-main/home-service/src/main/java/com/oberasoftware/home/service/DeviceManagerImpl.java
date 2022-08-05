package com.oberasoftware.home.service;

import com.oberasoftware.iot.core.ControllerConfiguration;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.managers.DeviceManager;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class DeviceManagerImpl implements DeviceManager {
    private static final Logger LOG = getLogger(DeviceManagerImpl.class);

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ControllerConfiguration controllerConfiguration;

    @Override
    public IotThing registerThing(IotThing thing) throws IOTException {
        LOG.debug("Registering device: {} on controller: {}", thing, thing.getControllerId());

        return homeDAO.findController(thing.getControllerId()).map(new Function<Controller, IotThing>() {
            @Override
            public IotThing apply(Controller controller) {
                try {
                    return itemManager.createOrUpdateThing(thing.getControllerId(), thing.getId(), thing.getPluginId(), thing.getParentId(), thing.getProperties());
                } catch (IOTException e) {
                    throw new RuntimeIOTException("Unable to store Thing: " + thing.getId() + " on controller: " + thing.getControllerId(), e);
                }
            }
        }).orElseThrow(() -> new IOTException("Unable to store Thing: " +
                thing.getId() + " on controller: " + thing.getControllerId() + " as controller not found"));
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String deviceId) {
        return homeDAO.findThing(controllerId, deviceId);
    }

    @Override
    public List<IotThing> getThings(String controllerId) {
        return homeDAO.findThings(controllerId);
    }

}
