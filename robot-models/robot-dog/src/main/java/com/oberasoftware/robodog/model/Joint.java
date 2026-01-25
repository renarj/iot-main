package com.oberasoftware.robodog.model;

/**
 * Represents a Joint in the robot dog's leg.
 */
public interface Joint {
    String getName();

    /**
     * Moves the joint to a specific angle in degrees.
     * @param degrees The target angle in degrees.
     */
    void moveToAngle(double degrees);

    /**
     * Gets the current angle of the joint in degrees.
     * @return The current angle in degrees.
     */
    double getCurrentAngle();
}
