package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.api.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import com.oberasoftware.robo.core.commands.ReadPositionAndSpeedCommand;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.robo.core.ConverterUtil.*;
import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static java.lang.String.valueOf;

/**
 * @author Renze de Vries
 */
@Component
@ConditionalOnProperty(value = "protocol.v2.enabled", havingValue = "true", matchIfMissing = false)
public class DynamixelV2ReadPositionHandler extends AbstractDynamixelHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelV2ReadPositionHandler.class);
    public static final double MA_CURRENT_UNIT = 2.69;

    @EventSubscribe
    public ServoDataReceivedEvent receive(ReadPositionAndSpeedCommand command) {
        int servoId = toSafeInt(command.getServoId());
        LOG.debug("Sending Read command for speed and position for servo: {}", servoId);

        byte[] data = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, servoId)
                .addInt16Bit(DynamixelV2Address.PRESENT_CURRENT, 0x0A)
                .build();
        LOG.debug("Requesting speed and position for servo: {} using request: {}", servoId, bb2hex(data));
        byte[] received = connector.sendAndReceive(data);
        LOG.debug("Received a speed and position reply: {} for servo: {}", bb2hex(received), servoId);

        DynamixelV2ReturnPacket returnPacket = new DynamixelV2ReturnPacket(received);
        if(!returnPacket.hasInstructionError()) {
            if(returnPacket.hasErrors()) {
                diagnoseStatusError(servoId, returnPacket, received);
            }

            byte[] params = returnPacket.getParameters();
            if(params.length == 10) {
                double current = (double)Math.abs(byteToInt(params[0], params[1])) * MA_CURRENT_UNIT;
                int speed = byteToInt32(params[2], params[3], params[4], params[5]);
                int position = byteToInt32(params[6], params[7], params[8], params[9]);

                LOG.debug("Servo: {} has position: {} and speed: {} with current: {}", servoId, position, speed, current);

                Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                        .put(ServoProperty.POSITION_SCALE, new Scale(0, 4095))
                        .put(ServoProperty.POSITION, position)
                        .put(ServoProperty.SPEED_SCALE, new Scale(-400, 400))
                        .put(ServoProperty.SPEED, speed)
                        .put(ServoProperty.CURRENT, current)
                        .build();

                return new ServoDataReceivedEvent(valueOf(servoId), new ServoDataImpl(command.getServoId(), map));
            } else {
                LOG.warn("Incorrect number of parameters for servo: {} in return package was: {} full package: {}", servoId, bb2hex(params), bb2hex(received));
            }
        } else {
            LOG.error("Instruction packet error: {} for data: {} for servo: {}", returnPacket.getErrorReason(), bb2hex(received), servoId);
        }

        return null;
    }
}
