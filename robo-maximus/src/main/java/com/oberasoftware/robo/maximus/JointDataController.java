package com.oberasoftware.robo.maximus;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.api.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.dynamixel.web.ServoStateController;
import com.oberasoftware.robo.maximus.impl.JointDataImpl;
import com.oberasoftware.robo.maximus.impl.MotionControlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @author Renze de Vries
 */
@Controller
public class JointDataController implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ServoStateController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventSubscribe
    public void receiveStateUpdate(ServoUpdateEvent stateUpdateEvent) {
        LOG.debug("Received servo update event: {}", stateUpdateEvent);
        ServoData data = stateUpdateEvent.getServoData();
        Integer position = data.getValue(ServoProperty.POSITION);
        Scale positionScale = data.getValue(ServoProperty.POSITION_SCALE);

        messagingTemplate.convertAndSend("/topic/joints",
                new JointDataImpl(stateUpdateEvent.getServoId(),
                        positionScale.convertToScale(position, MotionControlImpl.RADIAL_SCALE), position));
    }
}
