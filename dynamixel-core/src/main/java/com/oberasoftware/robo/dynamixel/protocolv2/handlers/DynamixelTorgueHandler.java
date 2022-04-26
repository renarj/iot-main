package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.commands.BulkTorgueCommand;
import com.oberasoftware.robo.api.commands.TorgueCommand;
import com.oberasoftware.robo.api.commands.TorgueLimitCommand;
import com.oberasoftware.robo.api.servo.events.ServoDataReceivedEvent;
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
