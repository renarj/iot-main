package com.oberasoftware.robo.maximus.controllers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.maximus.model.JointDataImpl;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @author Renze de Vries
 */
@Controller
public class JointWebsocketRelayController implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JointWebsocketRelayController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public JointWebsocketRelayController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventSubscribe
    public void receiveStateUpdate(JointDataImpl jointUpdateEvent) {
        LOG.debug("Received joint update event, relaying over websocket: {}", jointUpdateEvent);

        messagingTemplate.convertAndSend("/topic/joints",jointUpdateEvent);
    }

    @EventSubscribe
    public void receiveSensorUpdate(SensorDataImpl sensorUpdate) {
        LOG.debug("Received sensor update event, relaying over websocket: {}", sensorUpdate);
        messagingTemplate.convertAndSend("/topic/sensors", sensorUpdate);
    }
}
