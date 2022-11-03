package com.oberasoftware.home.core.state;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.events.StateUpdateEvent;
import com.oberasoftware.iot.core.model.ValueTransportMessage;
import com.oberasoftware.iot.core.model.states.Value;
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
        LOG.info("Received state: {}", stateUpdateEvent);

        var state = stateUpdateEvent.getState();
        String label = stateUpdateEvent.getAttribute();
        Value value = state.getStateItem(label).getValue();

        messagingTemplate.convertAndSend("/topic/state", new ValueTransportMessage(value,
                state.getControllerId(), stateUpdateEvent.getItemId(), label));
    }
}
