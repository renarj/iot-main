package com.oberasoftware.robo.dynamixel;

import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;

public interface BulkWriteMovementHandler {
    @EventSubscribe
    void receive(BulkPositionSpeedCommand command);
}
