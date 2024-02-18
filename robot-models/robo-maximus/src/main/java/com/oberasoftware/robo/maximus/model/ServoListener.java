package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.servo.ServoData;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.maximus.motion.JointControlImpl;
import org.slf4j.Logger;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class ServoListener implements EventHandler {
    private static final Logger LOG = getLogger(ServoListener.class);

    private final JointControl motionControl;

    ServoListener(JointControl motionControl) {
        this.motionControl = motionControl;
    }

    @EventSubscribe
    public JointDataImpl receiveStateUpdate(ServoUpdateEvent stateUpdateEvent) {
        LOG.debug("Received servo update event: {}", stateUpdateEvent);
        ServoData data = stateUpdateEvent.getServoData();

        if(motionControl.isJointPresent(stateUpdateEvent.getServoId())) {

            if (data.containsValue(ServoProperty.POSITION) && data.containsValue(ServoProperty.POSITION_SCALE)) {
                Integer position = data.getValue(ServoProperty.POSITION);
                Scale positionScale = data.getValue(ServoProperty.POSITION_SCALE);

                Joint joint = motionControl.getJoint(stateUpdateEvent.getServoId());
                int degrees = positionScale.convertToScale(position, JointControlImpl.RADIAL_SCALE);
                if (joint.isInverted()) {
                    LOG.debug("Joint is inverted: {}", joint);
                    degrees = -degrees;
                }

                Map<String, Object> map = ImmutableMap.<String, Object>builder()
                        .put(ServoProperty.POSITION.name().toLowerCase(), position)
                        .put("degrees", degrees)
                        .build();

                return new JointDataImpl(joint.getID(), map);
            } else if (data.containsValue(ServoProperty.TORGUE)) {
                Map<String, Object> map = ImmutableMap.<String, Object>builder()
                        .put(ServoProperty.TORGUE.name().toLowerCase(), data.getValue(ServoProperty.TORGUE))
                        .build();

                LOG.info("Received a torgue data update for servo: {} data: {}", stateUpdateEvent.getServoId(), map);

                return new JointDataImpl(data.getServoId(), map);
            } else {
                LOG.debug("Have no data for servo");
            }
        } else {
            LOG.error("Received a servo update for: {} but not mapped to JOint", stateUpdateEvent.getServoId());
        }
        return null;
    }
}
