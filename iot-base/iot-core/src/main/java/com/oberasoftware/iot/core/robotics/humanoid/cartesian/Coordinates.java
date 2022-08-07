package com.oberasoftware.iot.core.robotics.humanoid.cartesian;

public interface Coordinates {
    double getX();

    double getY();

    double getZ();

    CoordinatesBuilder move(double xDelta, double yDelta, double zDelta);
}
