package com.oberasoftware.home.hue.actions;

import com.oberasoftware.iot.core.AutomationBus;
import com.oberasoftware.home.hue.HueConnector;
import com.oberasoftware.home.hue.HueDeviceManager;
import com.oberasoftware.iot.core.events.impl.DeviceValueEventImpl;
import com.oberasoftware.iot.core.legacymodel.OnOffValue;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class SwitchCommandAction implements HueCommandAction<SwitchCommand> {
    private static final Logger LOG = getLogger(SwitchCommandAction.class);


    @Autowired
    private HueConnector hueConnector;


    @Autowired
    private HueDeviceManager deviceManager;

    @Autowired
    private AutomationBus automationBus;



    @Override
    public void receive(DeviceItem item, SwitchCommand switchCommand) {
        String bridgeId = item.getProperties().get("bridge");
        var light = deviceManager.findDevice(bridgeId, item.getDeviceId());

        LOG.debug("Received a switch command for bulb: {} desired state: {}", item.getDeviceId(), switchCommand.getState());
        if(light.isPresent()) {
            OnOffValue value = new OnOffValue(switchCommand.getState() == SwitchCommand.STATE.ON);

            LOG.info("Switching state of light: {} on bridge: {} to: {}", light.get().getId(), bridgeId, value);
            deviceManager.switchState(bridgeId, item.getDeviceId(), value);

            automationBus.publish(new DeviceValueEventImpl(item.getControllerId(), item.getDeviceId(),value, OnOffValue.LABEL));
        }
    }

    @Override
    public void receive(GroupItem groupItem, List<DeviceItem> items, SwitchCommand command) {
//        PHBridge bridge = hueConnector.getSdk().getSelectedBridge();
//
//        PHGroup group = GroupHelper.getOrCreateGroup(groupItem, hueConnector.getBridge(), items);
//        LOG.debug("Received a Switch group event: {} setting light state: {}", group, command.getState());
//
//        bridge.setLightStateForGroup(group.getIdentifier(), getTargetState(command));
//
//        Value value = new OnOffValue(command.getState() == SwitchCommand.STATE.ON);
//        items.forEach(i -> automationBus.publish(new ItemNumericValue(i.getId(), value, OnOffValue.LABEL)));
    }
//
//    private PHLightState getTargetState(SwitchCommand switchCommand) {
//        PHLightState st = new PHLightState();
//        st.setOn(switchCommand.getState() == SwitchCommand.STATE.ON);
//
//        return st;
//    }
}
