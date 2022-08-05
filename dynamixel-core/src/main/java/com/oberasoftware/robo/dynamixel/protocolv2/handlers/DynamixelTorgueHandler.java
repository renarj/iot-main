package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.BulkTorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueLimitCommand;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.robo.core.commands.ReadTorgueCommand;

public interface DynamixelTorgueHandler {
    @EventSubscribe
    ServoDataReceivedEvent receive(ReadTorgueCommand readTorgueCommand);

    @EventSubscribe
    void receive(TorgueCommand torgueCommand);

    @EventSubscribe
    void receive(BulkTorgueCommand torgueCommand);

    @EventSubscribe
    void receive(TorgueLimitCommand torgueLimitCommand);
}
