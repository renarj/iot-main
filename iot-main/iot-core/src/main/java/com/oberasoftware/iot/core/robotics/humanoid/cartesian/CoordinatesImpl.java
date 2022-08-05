package com.oberasoftware.iot.core.robotics.humanoid.cartesian;

public class CoordinatesImpl implements Coordinates {
    private final double x;
    private final double y;
    private final double z;

    public CoordinatesImpl(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public CoordinatesBuilder move(double xDelta, double yDelta, double zDelta) {
        var newX = getX() + xDelta;
        var newY = getY() + yDelta;
        var newZ = getZ() + zDelta;

        return new CoordinatesBuilder(newX, newY, newZ);
    }


    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
