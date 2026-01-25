package com.oberasoftware.robodog.model;

import java.util.Map;

/**
 * Interface representing a gait for the robot dog.
 * A gait defines the sequence of leg movements for walking.
 */
public interface Gait {
    /**
     * Executes one step of the gait.
     * @param legs The map of legs to move.
     * @param direction The direction and distance of the step (x, y).
     * @param stepHeight The height to lift the leg during the swing phase.
     * @param groundHeight The height of the foot when on the ground.
     * @param progress The progress of the current step (0.0 to 1.0).
     */
    void step(Map<String, Leg> legs, Vector3D direction, double stepHeight, double groundHeight, double progress);
}
