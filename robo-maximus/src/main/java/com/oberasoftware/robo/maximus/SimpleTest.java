package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.slf4j.Logger;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static org.slf4j.LoggerFactory.getLogger;

public class SimpleTest {
    private static final Logger LOG = getLogger(SimpleTest.class);


    public static void main(String[] args) {
        //{"command":"dynamixel","wait":"true","dxldata":"FFFF7C020180"}


        TeensyProxySerialConnector t = new TeensyProxySerialConnector();
        t.connect("/dev/tty.usbmodem61074701");

        byte[] pingPkt = new DynamixelV2CommandPacket(DynamixelInstruction.PING, 124).build();
        LOG.info("Received on ping: {}", bb2hex(t.sendAndReceive(pingPkt)));


        byte[] pkg = new DynamixelV2CommandPacket(DynamixelInstruction.WRITE_DATA, 124).add8BitParam(DynamixelV2Address.TORGUE_ENABLE, 0x01).build();
        LOG.info("Sending package: {}", bb2hex(pkg));

        //FFFFFD007C070003400001015D8A

        byte[] rcvd = t.sendAndReceive(pkg);

        LOG.info("Received hex: {} raw: {}", bb2hex(rcvd), new String(rcvd));

        DynamixelV2ReturnPacket p = new DynamixelV2ReturnPacket(rcvd);
        LOG.info("Return packet errors: {}, code: {}", p.hasErrors(), p.getErrorCode());




    }
}
