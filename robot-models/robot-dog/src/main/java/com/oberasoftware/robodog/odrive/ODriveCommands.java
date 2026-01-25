package com.oberasoftware.robodog.odrive;

/**
 * ODrive CANSimple command IDs (v0.5+).
 */
public enum ODriveCommands {
    HEARTBEAT(0x01),
    ESTOP(0x02),
    GET_ERROR(0x03),
    RX(0x04),
    TX(0x05),
    ADDRESS(0x06),
    SET_AXIS_STATE(0x07),
    GET_ENCODER_ESTIMATES(0x09),
    SET_CONTROLLER_MODES(0x0B),
    SET_INPUT_POS(0x0C),
    SET_INPUT_VEL(0x0D),
    SET_INPUT_TORQUE(0x0E),
    SET_LIMITS(0x0F),
    SET_TRAJ_LIMIT(0x11),
    GET_IQ(0x14),
    GET_TEMPERATURE(0x15),
    REBOOT_DRIVE(0x16),
    GET_BUS_VOLTAGE(0x17),
    SET_POS_GAIN(0x1A),
    SET_VEL_GAIN(0x1B);

    private final int value;

    ODriveCommands(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
