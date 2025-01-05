package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.commands.OperationModeCommand;
import com.oberasoftware.iot.core.robotics.commands.ReadOperationMode;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.StateManager;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import com.oberasoftware.robo.dynamixel.DynamixelConnector;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.OpsModeHandler;
import com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.robo.core.ConverterUtil.intTo16BitByte;
import static com.oberasoftware.robo.core.ConverterUtil.toSafeInt;
import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

@Component
@ConditionalOnProperty(value = "protocol.v2.enabled", havingValue = "true", matchIfMissing = true)
public class DynamixelV2OperationModeHandler implements EventHandler, OpsModeHandler {
    private static final Logger LOG = getLogger(DynamixelV2OperationModeHandler.class);


    @Autowired
    private DynamixelConnector connector;

    @EventSubscribe
    @Override
    public void receive(OperationModeCommand command) {
        int servoId = toSafeInt(command.getServoId());
        LOG.debug("Received a servo: {} operation mode command: {}", command.getServoId(), command);

        int dynamixelMode = switch (command.getOperationMode()) {
            case VELOCITY_CONTROL -> 1;
            case POSITION_CONTROL -> 3;
            case CURRENT_CONTROL -> 0;
        };

        DynamixelV2CommandPacket packet = new DynamixelV2CommandPacket(DynamixelInstruction.WRITE_DATA, servoId);
        packet.addParam(DynamixelV2Address.OPERATING_MODE, intTo16BitByte(dynamixelMode));

        byte[] data = packet.build();
        byte[] received = connector.sendAndReceive(data);
        LOG.info("Operating mode package has been delivered");

        DynamixelV2ReturnPacket returnPacket = new DynamixelV2ReturnPacket(received);
        if (!returnPacket.hasErrors()) {
            LOG.debug("Mode for servo: {} set to: {}", servoId, command);
        } else {
            LOG.info("Packet data: {}", bb2hex(received));
            LOG.error("Could not set servo: {} to operating mode: {} reason: {}", servoId, command, returnPacket.getErrorReason());
        }
    }

    @EventSubscribe
    @Override
    public ServoDataReceivedEvent receive(ReadOperationMode command) {
        LOG.info("Received a Servo Operation Mode read command: {}", command);
        int servoId = toSafeInt(command.getServoId());

        byte[] data = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, servoId)
                .addInt16Bit(DynamixelV2Address.OPERATING_MODE, 0x01)
                .build();
        byte[] received = connector.sendAndReceive(data);
        DynamixelV2ReturnPacket returnPacket = new DynamixelV2ReturnPacket(received);
        if(!returnPacket.hasInstructionError()) {
            LOG.trace("Received an Operating Mode reply: {} for servo: {}", DynamixelV1CommandPacket.bb2hex(received), servoId);

            byte[] params = returnPacket.getParameters();
            if (params.length == 1) {
                int dynamixelMode = params[0];
                StateManager.ServoMode opsMode = switch (dynamixelMode) {
                    case 0 -> StateManager.ServoMode.CURRENT_CONTROL;
                    case 1 -> StateManager.ServoMode.VELOCITY_CONTROL;
                    default -> StateManager.ServoMode.POSITION_CONTROL;
                };
                LOG.info("Servo: {} is in operating mode: {}", servoId, opsMode);
                Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                        .put(ServoProperty.MODE, opsMode.name())
                        .build();
                return new ServoDataReceivedEvent(valueOf(servoId), new ServoDataImpl(command.getServoId(), map));
            }
        }
        return null;
    }
}
