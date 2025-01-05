package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.iot.core.robotics.servo.events.ServoDataReceivedEvent;
import com.oberasoftware.robo.core.ServoDataImpl;
import com.oberasoftware.iot.core.robotics.commands.ReadTemperatureCommand;
import com.oberasoftware.robo.dynamixel.DynamixelConnector;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.robo.core.ConverterUtil.byteToInt;
import static com.oberasoftware.robo.core.ConverterUtil.toSafeInt;
import static com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1CommandPacket.bb2hex;
import static java.lang.String.valueOf;

/**
 * @author Renze de Vries
 */
@Component
@ConditionalOnProperty(value = "protocol.v2.enabled", havingValue = "true", matchIfMissing = true)
public class DynamixelV2ReadTemperatureHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelV2ReadTemperatureHandler.class);

    @Autowired
    private DynamixelConnector connector;

    @EventSubscribe
    public ServoDataReceivedEvent receive(ReadTemperatureCommand command) {
        LOG.debug("Received a read temperature command: {}", command);

        int servoId = toSafeInt(command.getServoId());

        byte[] data = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, servoId)
                .addInt16Bit(DynamixelV2Address.CURRENT_VOLTAGE, 0x03)
                .build();

        byte[] received = connector.sendAndReceive(data);
        DynamixelV2ReturnPacket returnPacket = new DynamixelV2ReturnPacket(received);
        if(!returnPacket.hasInstructionError()) {
            LOG.trace("Received a voltage and temperature reply: {} for servo: {}", bb2hex(received), servoId);

            byte[] params = returnPacket.getParameters();
            if(params.length == 3) {
                double voltage = (double)Math.abs(byteToInt(params[0], params[1])) * 0.1;
                int temperature = params[2];
                LOG.debug("Servo: {} has temperature: {} and voltage: {}", servoId, temperature, voltage);

                Map<ServoProperty, Object> map = new ImmutableMap.Builder<ServoProperty, Object>()
                        .put(ServoProperty.TEMPERATURE, temperature)
                        .put(ServoProperty.VOLTAGE, voltage)
                        .build();

                return new ServoDataReceivedEvent(valueOf(servoId), new ServoDataImpl(command.getServoId(), map));
            } else {
                LOG.warn("Incorrect number of parameters in return package was: {}", bb2hex(params));
            }
        } else {
            LOG.warn("Received an error: {} for temperature readout for servo: {} data: {}", returnPacket.getErrorCode(), servoId, bb2hex(received));
        }


        return null;

    }
}
