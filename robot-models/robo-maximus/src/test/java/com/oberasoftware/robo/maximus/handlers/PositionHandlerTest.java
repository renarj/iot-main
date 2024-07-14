package com.oberasoftware.robo.maximus.handlers;

import com.oberasoftware.iot.core.commands.ThingValueCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.robo.maximus.ServoRegistry;
import com.oberasoftware.robo.maximus.ThingKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PositionHandlerTest {

    @Mock
    private RobotRegistry robotRegistry;

    @Mock
    private ServoRegistry servoRegistry;

    @Mock
    private ServoDriver servoDriver;

    @Mock
    private RobotHardware robotHardware;

    @InjectMocks
    private PositionHandler positionHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should set servo position with speed when speed is present")
    public void shouldSetServoPositionWithSpeedWhenSpeedIsPresent() {
        Map<String, Value> attributes = new HashMap<>();
        attributes.put("position", new ValueImpl(VALUE_TYPE.NUMBER, 1000L));
        attributes.put("speed", new ValueImpl(VALUE_TYPE.NUMBER, 50L));

        ThingValueCommand command = new ValueCommandImpl("controllerId", "thingId", attributes);
        when(servoRegistry.getThing(any(), any())).thenReturn(new ThingKey("controllerId", "thingId", "servoId", "robotId"));
        when(robotRegistry.getRobot(any())).thenReturn(robotHardware);
        when(robotHardware.getServoDriver()).thenReturn(servoDriver);

        positionHandler.handle("position", command);

        verify(servoDriver).setPositionAndSpeed(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should set servo position without speed when speed is not present")
    public void shouldSetServoPositionWithoutSpeedWhenSpeedIsNotPresent() {
        Map<String, Value> attributes = new HashMap<>();
        attributes.put("position", new ValueImpl(VALUE_TYPE.NUMBER, 1000L));

        ThingValueCommand command = new ValueCommandImpl("controllerId", "thingId", attributes);
        when(servoRegistry.getThing(any(), any())).thenReturn(new ThingKey("controllerId", "thingId", "servoId", "robotId"));
        when(robotRegistry.getRobot(any())).thenReturn(robotHardware);
        when(robotHardware.getServoDriver()).thenReturn(servoDriver);

        positionHandler.handle("position", command);

        verify(servoDriver).setTargetPosition(any(), any(), any());
    }
}