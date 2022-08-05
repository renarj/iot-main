package com.oberasoftware.robo.dynamixel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.robotics.ActivatableCapability;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.commands.BulkTorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueLimitCommand;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.TorgueManager;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.iot.core.robotics.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import com.oberasoftware.robo.core.commands.ReadTorgueCommand;
import com.oberasoftware.robo.dynamixel.protocolv2.handlers.DynamixelTorgueHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.oberasoftware.iot.core.robotics.servo.TorgueManager.ServoState.OFF;
import static com.oberasoftware.iot.core.robotics.servo.TorgueManager.ServoState.ON;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DynamixelTorgueManager implements ActivatableCapability, TorgueManager, EventHandler {
    private static final Logger LOG = getLogger( DynamixelTorgueManager.class );

    private final Map<String, ServoState> servoStates = new ConcurrentHashMap<>();

    @Autowired
    private DynamixelTorgueHandler torgueHandler;

    @Autowired
    private LocalEventBus localEventBus;

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        LOG.info("Reading initial Torgue states");
        robot.getServoDriver().getServos().forEach(s -> {
            ServoDataReceivedEvent rcvd = torgueHandler.receive(new ReadTorgueCommand(s.getId()));

            int tState = rcvd.getServoData().getValue(ServoProperty.TORGUE);
            servoStates.put(s.getId(), tState > 0 ? ON : OFF);
        });
    }

    public void setTorgue(String s, int limit) {
        setState(s, true);

        torgueHandler.receive(new TorgueCommand(s, true));
        torgueHandler.receive(new TorgueLimitCommand(s, limit));
    }

    public void setTorgue(String s, boolean b) {
        setState(s, b);

        torgueHandler.receive(new TorgueCommand(s, b));
    }

    public void setTorgueAll(boolean state) {
        servoStates.keySet().forEach(s -> setState(s, state));

        torgueHandler.receive(new BulkTorgueCommand(state));
    }

    public void setTorgueAll(boolean state, List<String> servos) {
        servos.forEach(s -> setState(s, state));

        torgueHandler.receive(new BulkTorgueCommand(state, servos));
    }

    private void setState(String servoId, boolean state) {
        setState(servoId, state ? ON : OFF);
    }

    private void setState(String servoId, ServoState state) {
        servoStates.put(servoId, state);

        Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                .put(ServoProperty.TORGUE, state == ON ? 1 : 0)
                .build();

        localEventBus.publish(new ServoUpdateEvent(servoId, new ServoDataImpl(servoId, map)));
    }

    @Override
    public ServoState getState(String servoId) {
        return servoStates.get(servoId);
    }

    @Override
    public Map<String, ServoState> getStates() {
        return Maps.newHashMap(servoStates);
    }

    @Override
    public void shutdown() {

    }
}
