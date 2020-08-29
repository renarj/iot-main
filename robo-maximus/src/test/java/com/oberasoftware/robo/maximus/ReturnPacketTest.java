package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.codec.Hex;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnPacketTest {
    @Test
    public void testReturnErrorCode() {
        String pkg = "FF FF FD 00 66 0E 00 55 80 F7 FF E7 FF FF FF 6A 0A 00 00 7C 32";

        byte[] decoded = Hex.decode(pkg.replaceAll(" ", ""));
        assertEquals(bb2hex(decoded), pkg);

        DynamixelV2ReturnPacket pkt = new DynamixelV2ReturnPacket(decoded);
        assertEquals(pkt.getErrorCode(), 128);
        assertEquals("Hardware alert; ", pkt.getErrorReason());

        assertEquals(pkt.getParameters().length, 10);
    }
}
