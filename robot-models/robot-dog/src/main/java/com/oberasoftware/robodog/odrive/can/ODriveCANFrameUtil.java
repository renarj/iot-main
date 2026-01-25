package com.oberasoftware.robodog.odrive.can;

import com.oberasoftware.robodog.odrive.ControlMode;
import com.oberasoftware.robodog.odrive.InputMode;
import com.oberasoftware.robodog.odrive.ODriveCommands;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ODriveCANFrameUtil {
    public static final int BROADCAST_ID = 0x03F;

    public static int arbitrationId(int nodeId, int cmd) {
        return (nodeId << 5) | cmd;
    }

    public static int arbitrationId(int nodeId, ODriveCommands command) {
        return arbitrationId(nodeId, command.getValue());
    }

    public static CanFrame discoverFrame() {
        int arbId = arbitrationId(BROADCAST_ID, ODriveCommands.ADDRESS);

        return CanFrame.create(arbId, CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame setAxisState(int nodeId, int state) {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(state);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_AXIS_STATE), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame setControllerModes(int nodeId, ControlMode controlMode, InputMode inputMode) {
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(controlMode.getValue());
        buf.putInt(inputMode.getValue());
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_CONTROLLER_MODES), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame sendWriteRXCommand(int nodeId, int endpointId, float value) {
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)0x1);
        buf.putShort((short)endpointId);
        buf.put((byte)0);
        buf.putFloat(value);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.RX), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame sendReadRXCommand(int nodeId, int endpointId) {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)0);
        buf.putShort((short)endpointId);
        buf.put((byte)0);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.RX), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame setInputPosition(int nodeId, float turns, float velFF, float torqueFF) {
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(turns);
        // In CANSimple, vel_ff and torque_ff are 16-bit each; here we keep 0 for simplicity
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_INPUT_POS), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame setInputVelocity(int nodeId, float vel, float torqueFF) {
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(vel);
        // In CANSimple, torque_ff is 32-bit float for input vel
        buf.putFloat(torqueFF);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_INPUT_VEL), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame setLimits(int nodeId, float velLimit, float curLimit) {
        ByteBuffer p = ByteBuffer.allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(velLimit)
                .putFloat(curLimit);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_LIMITS),
                CanFrame.FD_NO_FLAGS, p.array());
    }

    public static CanFrame setTrajLimits(int nodeId, float limit) {
        ByteBuffer p = ByteBuffer.allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(limit);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_TRAJ_LIMIT), CanFrame.FD_NO_FLAGS, p.array());
    }

    public static CanFrame rebootDrive(int nodeId) {
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.REBOOT_DRIVE), CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame requestBusVoltage(int nodeId) {
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.GET_BUS_VOLTAGE), CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame requestTemperature(int nodeId) {
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.GET_TEMPERATURE), CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame requestEncoderEstimates(int nodeId) {
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.GET_ENCODER_ESTIMATES), CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame requestIq(int nodeId) {
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.GET_IQ), CanFrame.FD_NO_FLAGS, new byte[0]);
    }

    public static CanFrame setPositionGain(int nodeId, float gain) {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(gain);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_POS_GAIN), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static CanFrame setVelocityGain(int nodeId, float gain, float integratorGain) {
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(gain);
        buf.putFloat(integratorGain);
        return CanFrame.create(arbitrationId(nodeId, ODriveCommands.SET_VEL_GAIN), CanFrame.FD_NO_FLAGS, buf.array());
    }

    public static String bb2hex(byte[] buffer) {
        return bb2hex(buffer, true);
    }

    public static String bb2hex(byte[] buffer, boolean formatSpaced) {
        StringBuilder result = new StringBuilder();
        for (byte b : buffer) {
            result.append(String.format("%02X", b));
            if(formatSpaced) {
                result.append(" ");
            }
        }
        return result.toString().trim();
    }
}
