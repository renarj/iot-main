package com.oberasoftware.robo.dynamixel.protocolv2;

import com.oberasoftware.robo.core.ConverterUtil;
import com.oberasoftware.robo.dynamixel.DynamixelReturnPacket;

import java.util.Arrays;
import java.util.BitSet;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;

/**
 * @author Renze de Vries
 */
public class DynamixelV2ReturnPacket implements DynamixelReturnPacket {
    private static final int FIXED_PARAM_START_OFFSET = 9;
    public static final int PACKET_WINDOW_SIZE_INFO = 4;

    public static final int HARDWARE_ALERT = 7;

    private final byte[] data;

    private final int id;
    private final int length;
    private final int errorCode;

    public DynamixelV2ReturnPacket(byte[] data) {
        if(data.length >= 9) {
            this.data = data;
            this.id = data[4];
            this.length = ConverterUtil.byteToInt(data[5], data[6]);
            this.errorCode = Byte.toUnsignedInt(data[8]);
        } else {
            this.data = data;
            this.length = 0;
            this.errorCode = -1;
            this.id = 0;
        }
    }

    public int getId() {
        return this.id;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorReason() {
        return getErrorReason(errorCode);
    }

    public boolean checkErrorBit(int bitIndex) {
        if(errorCode > -1) {
            BitSet s = BitSet.valueOf(new byte[]{(byte) errorCode});
            return s.get(bitIndex);
        } else {
            return false;
        }
    }

    public String getErrorReason(int errorCode) {
        if(errorCode > -1) {
            StringBuilder builder = new StringBuilder();
            BitSet s = BitSet.valueOf(new byte[]{(byte) errorCode});
            if(s.get(HARDWARE_ALERT)) {
                builder.append("Hardware alert; ");
            }
            if (s.get(6)) {
                builder.append("Access error; ");
            }
            if (s.get(5)) {
                builder.append("Data limit error; ");
            }
            if (s.get(4)) {
                builder.append("Data length error; ");
            }
            if (s.get(3)) {
                builder.append("Data Range error; ");
            }
            if (s.get(2)) {
                builder.append("CRC error; ");
            }
            if (s.get(1)) {
                builder.append("Instruction error; ");
            }
            if (s.get(0)) {
                builder.append("Failed to process; ");
            }

            return builder.toString();
        } else if(data.length == 0) {
            return "data package was 0 bytes";
        } else {
            return "unknown error: " + errorCode;
        }
    }

    public boolean hasErrors() {
        return errorCode != 0;
    }

    public boolean hasInstructionError() {
        return !checkErrorBit(HARDWARE_ALERT) && hasErrors();
    }

    public byte[] getParameters() {
        return Arrays.copyOfRange(data, FIXED_PARAM_START_OFFSET, (FIXED_PARAM_START_OFFSET + (length - PACKET_WINDOW_SIZE_INFO)));
    }

    @Override
    public String toString() {
        return "DynamixelV2ReturnPacket{" +
                "data=" + bb2hex(data) +
                ", id=" + id +
                ", length=" + length +
                ", params=[" + bb2hex(getParameters()) + "]" +
                '}';
    }
}
