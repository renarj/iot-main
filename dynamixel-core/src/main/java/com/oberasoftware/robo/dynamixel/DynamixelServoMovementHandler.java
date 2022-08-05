package com.oberasoftware.robo.dynamixel;

import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionCommand;
import com.oberasoftware.iot.core.robotics.commands.SpeedCommand;

public interface DynamixelServoMovementHandler {
    @EventSubscribe
    void receive(PositionCommand positionCommand);

    @EventSubscribe
    void receive(SpeedCommand speedCommand);

    @EventSubscribe
    void receive(PositionAndSpeedCommand command);
}
