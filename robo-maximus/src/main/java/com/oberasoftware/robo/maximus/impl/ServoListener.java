package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.api.servo.events.ServoUpdateEvent;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ServoListener implements EventHandler {
    private static final Logger LOG = getLogger(ServoListener.class);

    private final MotionControl motionControl;

    ServoListener(MotionControl motionControl) {
        this.motionControl = motionControl;
    }

    @EventSubscribe
    public JointDataImpl receiveStateUpdate(ServoUpdateEvent stateUpdateEvent) {
        LOG.debug("Received servo update event: {}", stateUpdateEvent);
        ServoData data = stateUpdateEvent.getServoData();

        if(data.containsValue(ServoProperty.POSITION) && data.containsValue(ServoProperty.POSITION_SCALE)) {
            Integer position = data.getValue(ServoProperty.POSITION);
            Scale positionScale = data.getValue(ServoProperty.POSITION_SCALE);

            Joint joint = motionControl.getJoint(stateUpdateEvent.getServoId());
            int degrees = positionScale.convertToScale(position, MotionControlImpl.RADIAL_SCALE);
            if(joint.isInverted()) {
                LOG.debug("Joint is inverted: {}", joint);
                degrees = -degrees;
            }

            return new JointDataImpl(joint.getID(), degrees, position);
        } else {
            LOG.info("Have no data for servo");
            return null;
        }
    }
}
