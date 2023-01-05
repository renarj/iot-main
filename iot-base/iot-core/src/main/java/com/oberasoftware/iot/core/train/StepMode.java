package com.oberasoftware.iot.core.train;

public enum StepMode {
    DCC_128(0x13),
    DCC_28(0x12),
    DCC_27(0x12),
    DCC_14(0x10);

    private int railFormat;

    StepMode(int railFormat) {
        this.railFormat = railFormat;
    }

    public int getFormat() {
        return this.railFormat;
    }
}
