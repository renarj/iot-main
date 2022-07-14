package com.oberasoftware.trainautomation.rocoz21;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.robo.core.ConverterUtil;
import org.slf4j.Logger;

import java.util.Arrays;

import static com.oberasoftware.trainautomation.rocoz21.commands.Z21Command.bytesToHex;
import static org.slf4j.LoggerFactory.getLogger;

public class Z21ReturnPacket implements Event {
    private static final Logger LOG = getLogger( Z21ReturnPacket.class );
    private static final int FIXED_PARAM_START_OFFSET = 4;
    private static final int X_HEADER = 0x40;

    private final byte[] data;

    private final int responseHeader;
    private final int length;

    private int xHeader = 0;

    public Z21ReturnPacket(byte[] data) {
        if(data.length > 4) {
            this.length = ConverterUtil.byteToInt(data[0], data[1]);
            this.responseHeader = ConverterUtil.byteToInt(data[2], data[3]);

            this.data = Arrays.copyOfRange(data, FIXED_PARAM_START_OFFSET, (FIXED_PARAM_START_OFFSET + (length - FIXED_PARAM_START_OFFSET)));

            if(this.responseHeader == X_HEADER) {
                this.xHeader = this.data[0];
            }
        } else {
            LOG.error("Invalid Z21 package received");
            this.length = 0;
            this.responseHeader = 0;
            this.data = new byte[0];
        }
    }

    public byte[] getData() {
        return data;
    }

    public int getResponseHeader() {
        return responseHeader;
    }

    public int getLength() {
        return length;
    }

    public int getxHeader() {
        return xHeader;
    }

    @Override
    public String toString() {
        return "Z21ReturnPacket{" +
                "data=" + bytesToHex(data) +
                ", responseHeader=" + String.format("%02X", responseHeader) +
                ", length=" + length +
                ", xHeader=" + String.format("%02X", xHeader) +
                '}';
    }
}
