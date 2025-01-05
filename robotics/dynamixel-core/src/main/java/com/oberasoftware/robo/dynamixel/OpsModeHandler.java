package com.oberasoftware.robo.dynamixel;

import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.OperationModeCommand;
import com.oberasoftware.iot.core.robotics.commands.ReadOperationMode;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;

public interface OpsModeHandler {
    @EventSubscribe
    ServoDataReceivedEvent receive(ReadOperationMode readOperationMode);

    @EventSubscribe
    void receive(OperationModeCommand operationModeCommand);
}
