package com.oberasoftware.iot.core.robotics.humanoid.cartesian;

import java.util.concurrent.TimeUnit;

public class CartesianMoveInput {
    public enum MOVE_MODE {
        ABSOLUTE,
        RELATIVE
    }

    private final double x;
    private final double y;

    private final double z;
    private final long time;

    private final TimeUnit unit;

    private final MOVE_MODE mode;

    public CartesianMoveInput(double x, double y, double z, long time, TimeUnit unit, MOVE_MODE mode) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
        this.unit = unit;
        this.mode = mode;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public MOVE_MODE getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "CartesianMoveInput{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", time=" + time +
                ", unit=" + unit +
                ", mode=" + mode +
                '}';
    }
}
