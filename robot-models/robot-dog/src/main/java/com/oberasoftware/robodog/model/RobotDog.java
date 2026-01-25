package com.oberasoftware.robodog.model;

import java.util.List;

public interface RobotDog {
    List<Leg> getLegs();

    Leg getLeg(String name);

    /**
     * Moves the body to a specific posture.
     * @param posture The target posture
     */
    void moveBody(Posture posture);

    /**
     * Walks the dog to a specific distance.
     * @param x The distance in meters in x direction
     * @param y The distance in meters in y direction
     */
    void walk(double x, double y);

    /**
     * Stops the robot dog and brings it to a safe state.
     */
    void stop();

    enum Posture {
        UP, DOWN, WALK
    }
}
