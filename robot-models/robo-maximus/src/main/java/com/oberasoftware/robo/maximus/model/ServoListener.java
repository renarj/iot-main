package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
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

        if(motionControl.isServoJointPresent(stateUpdateEvent.getServoId())) {

            if (data.containsValue(ServoProperty.POSITION) && data.containsValue(ServoProperty.POSITION_SCALE)) {
                Integer position = data.getValue(ServoProperty.POSITION);
                Scale positionScale = data.getValue(ServoProperty.POSITION_SCALE);

                var joint = motionControl.getServoJoint(stateUpdateEvent.getServoId());
                int degrees = positionScale.convertToScale(position, JointControlImpl.RADIAL_SCALE);
                if (joint.isInverted()) {
                    LOG.debug("Joint is inverted: {}", joint);
                    degrees = -degrees;
                }

                Map<String, Value> map = ImmutableMap.<String, Value>builder()
                        .put(ServoProperty.POSITION.name().toLowerCase(), new ValueImpl(VALUE_TYPE.NUMBER, position))
                        .put("degrees", new ValueImpl(VALUE_TYPE.NUMBER, degrees))
                        .build();

                return new JointDataImpl(joint.getControllerId(), joint.getJointId(), map);
            } else if (data.containsValue(ServoProperty.TORGUE)) {
                Map<String, Value> map = ImmutableMap.<String, Value>builder()
                        .put(ServoProperty.TORGUE.name().toLowerCase(), new ValueImpl(VALUE_TYPE.BOOLEAN, data.getValue(ServoProperty.TORGUE)))
                        .build();
                var joint = motionControl.getServoJoint(stateUpdateEvent.getServoId());

                LOG.info("Received a torgue data update for servo: {} data: {}", stateUpdateEvent.getServoId(), map);

                return new JointDataImpl(joint.getControllerId(), joint.getJointId(), map);
            } else {
                LOG.debug("Have no data for servo");
            }
        } else {
            LOG.debug("Received a servo update for: {} but not mapped to Joint", stateUpdateEvent.getServoId());
        }
        return null;
    }
}
