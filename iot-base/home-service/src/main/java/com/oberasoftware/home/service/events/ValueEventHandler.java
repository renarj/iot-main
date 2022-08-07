package com.oberasoftware.home.service.events;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.events.DeviceValueEvent;
import com.oberasoftware.iot.core.events.ItemValueEvent;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ValueEventHandler implements EventHandler {
    private static final Logger LOG = getLogger(ValueEventHandler.class);

    @Autowired
    private StateManager stateManager;

    @Autowired
    private DeviceManager deviceManager;

    @EventSubscribe
    public void receive(DeviceValueEvent event) {
        LOG.debug("Received a device value event: {}", event);

        Optional<IotThing> optionalItem = deviceManager.findThing(event.getControllerId(), event.getDeviceId());
        if (optionalItem.isPresent()) {
            LOG.debug("Updating state for device: {}", optionalItem);
            stateManager.updateDeviceState(optionalItem.get(), event.getLabel(), event.getValue());
        } else {
            LOG.warn("Received state for unknow device: {}", event);
        }
    }

    @EventSubscribe
    public void receive(ItemValueEvent event) {
        LOG.debug("Received an item value event: {}", event);
        String label = event.getLabel();
        Value value = event.getValue();

        LOG.debug("Updating state of group: {}", event.getItemId());
        stateManager.updateItemState(event.getItemId(), label, value);
    }
}
