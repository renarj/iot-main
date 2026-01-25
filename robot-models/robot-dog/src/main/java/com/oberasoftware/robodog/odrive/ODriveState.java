package com.oberasoftware.robodog.odrive;

public enum ODriveState {
    UNDEFINED(0x0),
    IDLE(0x1),
    STARTUP_SEQUENCE(0x2),
    FULL_CALIBRATION_SEQUENCE(0x3),
    MOTOR_CALIBRATION(0x4),
    ENCODER_INDEX_SEARCH(0x6),
    ENCODER_OFFSET_CALIBRATION(0x7),
    CLOSED_LOOP_CONTROL(0x8),
    LOCKIN_SPIN(0x9),
    ENCODER_DIR_FIND(0xA),
    HOMING(0xB),
    ENCODER_HALL_POLARITY_CALIBRATION(0xC),
    ENCODER_HALL_PHASE_CALIBRATION(0xD);

    private final int code;

    ODriveState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
