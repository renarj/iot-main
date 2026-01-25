package com.oberasoftware.robodog.odrive;

public enum InputMode {
    INACTIVE(0x0),
    PASSTHROUGH(0x1),
    VEL_RAMP(0x2),
    POS_FILTER(0x3),
    TRAP_TRAJ(0x5);

    private final int value;

    InputMode(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
