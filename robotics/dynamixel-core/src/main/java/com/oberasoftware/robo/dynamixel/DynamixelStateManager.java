package com.oberasoftware.robo.dynamixel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.robotics.ActivatableCapability;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.commands.*;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.StateManager;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.iot.core.robotics.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.oberasoftware.iot.core.robotics.servo.StateManager.TorgueState.OFF;
import static com.oberasoftware.iot.core.robotics.servo.StateManager.TorgueState.ON;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DynamixelStateManager implements ActivatableCapability, StateManager, EventHandler {
    private static final Logger LOG = getLogger( DynamixelStateManager.class );

    private final Map<String, TorgueState> servoStates = new ConcurrentHashMap<>();
    private final Map<String, StateManager.ServoMode> servoModes = new ConcurrentHashMap<>();

    @Autowired
    private DynamixelTorgueHandler torgueHandler;

    @Autowired
    private OpsModeHandler opsModeHandler;

    @Autowired
    private LocalEventBus localEventBus;

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        LOG.info("Reading initial Servo states");
        robot.getServoDriver().getServos().forEach(s -> {
            ServoDataReceivedEvent rcvd = torgueHandler.receive(new ReadTorgueCommand(s.getId()));

            int tState = rcvd.getServoData().getValue(ServoProperty.TORGUE);
            servoStates.put(s.getId(), tState > 0 ? ON : OFF);

            var opsModeData = opsModeHandler.receive(new ReadOperationMode(s.getId()));
            if(opsModeData != null) {
                servoModes.put(s.getId(), StateManager.ServoMode.valueOf(opsModeData.getServoData().getValue(ServoProperty.MODE)));
            }
        });
    }

    public void setTorgue(String s, int limit) {
        setState(s, true);

        torgueHandler.receive(new TorgueCommand(s, true));
        torgueHandler.receive(new TorgueLimitCommand(s, limit));
    }

    @Override
    public void setTorgue(String s, boolean b) {
        setState(s, b);

        torgueHandler.receive(new TorgueCommand(s, b));
    }

    @Override
    public void setTorgueAll(boolean state) {
        servoStates.keySet().forEach(s -> setState(s, state));

        torgueHandler.receive(new BulkTorgueCommand(state));
    }

    @Override
    public void setTorgueAll(boolean state, List<String> servos) {
        servos.forEach(s -> setState(s, state));

        torgueHandler.receive(new BulkTorgueCommand(state, servos));
    }

    private void setState(String servoId, boolean state) {
        setState(servoId, state ? ON : OFF);
    }

    private void setState(String servoId, TorgueState state) {
        servoStates.put(servoId, state);

        Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                .put(ServoProperty.TORGUE, state == ON ? 1 : 0)
                .build();

        localEventBus.publish(new ServoUpdateEvent(servoId, new ServoDataImpl(servoId, map)));
    }

    @Override
    public TorgueState getTorgue(String servoId) {
        return servoStates.get(servoId);
    }

    @Override
    public Map<String, TorgueState> getTorgues() {
        return Maps.newHashMap(servoStates);
    }

    @Override
    public void setServoMode(String servoId, ServoMode mode) {
        if(servoModes.containsKey(servoId) && servoModes.get(servoId) != mode) {
            servoModes.put(servoId, mode);

            LOG.info("Setting servo mode {} to state: {}", servoId, mode);
            opsModeHandler.receive(new OperationModeCommand(servoId, mode));
        } else {
            LOG.warn("Servo: {} mode is already on target mode state: {}", servoId, mode);
        }
    }

    @Override
    public ServoMode getServoMode(String servoId) {
        return servoModes.get(servoId);
    }

    @Override
    public Map<String, ServoMode> getServoModes() {
        return servoModes;
    }

    @Override
    public void shutdown() {

    }
}
