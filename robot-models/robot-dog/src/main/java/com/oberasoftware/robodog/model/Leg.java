package com.oberasoftware.robodog.model;

/**
 * Represents a Leg of the robot dog.
 * Each leg has three joints: Knee, Hip Pitch, and Hip Roll.
 */
public interface Leg {
    String getName();

    Joint getKneeJoint();

    Joint getHipPitchJoint();

    Joint getHipRollJoint();

    /**
     * Moves the foot to a specific coordinate relative to the hip.
     * @param position The target position (x, y, z)
     */
    void moveFootTo(Vector3D position);
}
