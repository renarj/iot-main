package com.oberasoftware.home.core.state;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.model.Value;
import com.oberasoftware.robo.core.events.devices.StateUpdateEvent;
import com.oberasoftware.robo.core.model.ValueTransportMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Controller
public class WebsocketController implements EventHandler {
    private static final Logger LOG = getLogger(WebsocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventSubscribe
    public void receiveStateUpdate(StateUpdateEvent stateUpdateEvent) {
        LOG.debug("Received state: {}", stateUpdateEvent);

        String label = stateUpdateEvent.getLabel();
        Value value = stateUpdateEvent.getState().getStateItem(label).getValue();

        messagingTemplate.convertAndSend("/topic/state", new ValueTransportMessage(value,
                stateUpdateEvent.getControllerId(), stateUpdateEvent.getItemId(), label));
    }
}
