package com.oberasoftware.robo.hexapod;

public class PosData {
    private final double x;
    private final double y;
    private final double z;

    public PosData(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public String toString() {
        return "PosData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
