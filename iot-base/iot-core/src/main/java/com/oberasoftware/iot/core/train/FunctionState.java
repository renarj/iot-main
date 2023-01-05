package com.oberasoftware.iot.core.train;

public enum FunctionState {
    ON(0x01),
    OFF(0x00),
    TOGGLE(0x10);

    private int state;

    FunctionState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
