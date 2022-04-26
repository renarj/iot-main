package com.oberasoftware.robo.dynamixel.protocolv2.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.robo.dynamixel.DynamixelConnector;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.BitSet;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket.HARDWARE_ALERT;

@Component
public abstract class AbstractDynamixelHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDynamixelHandler.class);

    @Autowired
    protected DynamixelConnector connector;

    protected void diagnoseStatusError(int servoId, DynamixelV2ReturnPacket returnPacket, byte[] received) {
        LOG.error("Received an error: {} for servo: {} data: {}", returnPacket.getErrorReason(), servoId, bb2hex(received));

        if(returnPacket.checkErrorBit(HARDWARE_ALERT)) {
            LOG.error("Received a hardware alert, checking hardware status");
            byte[] de = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, servoId)
                    .addInt16Bit(DynamixelV2Address.HARDWARE_ERROR_STATUS, 0x01)
                    .build();
            byte[] re = connector.sendAndReceive(de);
            LOG.debug("Received Servo: {} shutdown readout: {}", servoId, bb2hex(re));

            DynamixelV2ReturnPacket statusReply = new DynamixelV2ReturnPacket(re);
            byte[] params = statusReply.getParameters();
            if(params.length > 0) {
                LOG.error("Hardware error: {} for servo: {}", getErrorReason(params[0]), servoId);
            }
        } else {
            LOG.error("Could not diagnose servo status: {} was instruction error: {}", servoId, returnPacket.getErrorReason());
        }
    }

    public static String getErrorReason(int errorCode) {
        if(errorCode > -1) {
            StringBuilder builder = new StringBuilder();
            BitSet s = BitSet.valueOf(new byte[]{(byte) errorCode});
            if (s.get(5)) {
                builder.append("Overload error; ");
            }
            if (s.get(4)) {
                builder.append("Electrical Shock; ");
            }
            if (s.get(3)) {
                builder.append("Motor encoder error; ");
            }
            if (s.get(2)) {
                builder.append("Overheating error; ");
            }
            if (s.get(0)) {
                builder.append("Input voltage error; ");
            }

            return builder.toString();
        } else {
            return "unknown error: " + errorCode;
        }
    }
}
