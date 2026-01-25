package com.oberasoftware.robodog.odrive;

public enum ControlMode {
    VOLTAGE_CONTROL(0x0),
    TORQUE_CONTROL(0x1),
    VELOCITY_CONTROL(0x2),
    POSITION_CONTROL(0x3);

    private final int value;

    ControlMode(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
