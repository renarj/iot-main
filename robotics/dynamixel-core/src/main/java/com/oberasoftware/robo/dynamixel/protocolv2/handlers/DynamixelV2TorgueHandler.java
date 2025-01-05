package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.BulkTorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueCommand;
import com.oberasoftware.iot.core.robotics.commands.TorgueLimitCommand;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import com.oberasoftware.iot.core.robotics.commands.ReadTorgueCommand;
import com.oberasoftware.robo.dynamixel.DynamixelAddress;
import com.oberasoftware.robo.dynamixel.DynamixelConnector;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.DynamixelTorgueHandler;
import com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.robo.core.ConverterUtil.intTo16BitByte;
import static com.oberasoftware.robo.core.ConverterUtil.toSafeInt;
import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.BROADCAST_ID;
import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;

/**
 * @author Renze de Vries
 */
@Component
@ConditionalOnProperty(value = "protocol.v2.enabled", havingValue = "true", matchIfMissing = true)
public class DynamixelV2TorgueHandler implements EventHandler, DynamixelTorgueHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelV2TorgueHandler.class);

    @Autowired
    private DynamixelConnector connector;

    @Override
    @EventSubscribe
    public ServoDataReceivedEvent receive(ReadTorgueCommand command) {
        LOG.info("Received a torgue read command: {}", command);
        int servoId = toSafeInt(command.getServoId());

        byte[] data = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, servoId)
                .addInt16Bit(DynamixelV2Address.TORGUE_ENABLE, 0x01)
                .build();
        byte[] received = connector.sendAndReceive(data);
        DynamixelV2ReturnPacket pkt = new DynamixelV2ReturnPacket(received);
        if(!pkt.hasInstructionError()) {
            LOG.trace("Received a torgue reply: {} for servo: {}", DynamixelV1CommandPacket.bb2hex(received), servoId);

            byte[] params = pkt.getParameters();
            if (params.length == 1) {
                int torgue = params[0];
                LOG.debug("Readout of Torgue state is: {} for servo: {}", torgue, servoId);

                Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                        .put(ServoProperty.TORGUE, torgue)
                        .build();

                return new ServoDataReceivedEvent(command.getServoId(), new ServoDataImpl(command.getServoId(), map));
            }
        }

        return null;
    }

    @Override
    @EventSubscribe
    public void receive(TorgueCommand torgueCommand) {
        LOG.info("Received a torgue command: {}", torgueCommand);
        int servoId = toSafeInt(torgueCommand.getServoId());

        int targetTorgueState = getTargetTorgueState(torgueCommand.isEnableTorque());
        LOG.debug("Setting torgue to: {} for servo: {}", targetTorgueState, servoId);

        byte[] response = connector.sendAndReceive(new DynamixelV2CommandPacket(DynamixelInstruction.WRITE_DATA, servoId)
                .add8BitParam(DynamixelV2Address.TORGUE_ENABLE, targetTorgueState)
                .build());

        DynamixelV2ReturnPacket packet = new DynamixelV2ReturnPacket(response);
        if(packet.hasErrors()) {
            LOG.error("Could not set torgue response: {} for servo: {}", bb2hex(response), servoId);
        }
        LOG.debug("Received torgue response: {} for servo: {} errors: {} reason: {}", bb2hex(response), servoId, packet.hasErrors(), packet.getErrorReason());
    }

    @Override
    public void receive(BulkTorgueCommand torgueCommand) {
        LOG.info("Received a bulk torgue command: {}", torgueCommand);

        if(torgueCommand.getServos().isEmpty()) {
            //assume broadcast to all servos
            connector.sendNoReceive(new DynamixelV2CommandPacket(DynamixelInstruction.WRITE_DATA, BROADCAST_ID)
                    .add8BitParam(DynamixelV2Address.TORGUE_ENABLE, getTargetTorgueState(torgueCommand.isTorgueState()))
                    .build());
        } else {
            DynamixelV2CommandPacket cmd = new DynamixelV2CommandPacket(DynamixelInstruction.SYNC_WRITE, 0xFE);
            cmd.addParam(DynamixelV2Address.TORGUE_ENABLE, (byte)0x01, (byte)0x00);
            torgueCommand.getServos().forEach(s -> {
                cmd.add8BitParam(toSafeInt(s));
                cmd.add8BitParam(getTargetTorgueState(torgueCommand.isTorgueState()));
            });
            byte[] pkg = cmd.build();

            LOG.info("Sending sync write torgue command: {}", bb2hex(pkg));
            connector.sendNoReceive(pkg);
        }
    }

    private int getTargetTorgueState(boolean state) {
        return state ? 0x01 : 0x00;
    }

    @Override
    @EventSubscribe
    public void receive(TorgueLimitCommand torgueLimitCommand) {
        LOG.info("Received a torgue limit command: {}", torgueLimitCommand);

        int servoId = toSafeInt(torgueLimitCommand.getServoId());

        connector.sendAndReceive(new DynamixelV1CommandPacket(DynamixelInstruction.WRITE_DATA, servoId)
                .addParam(DynamixelAddress.TORGUE_LIMIT_L, intTo16BitByte(torgueLimitCommand.getTorgueLimit()))
                .build());
    }
}
