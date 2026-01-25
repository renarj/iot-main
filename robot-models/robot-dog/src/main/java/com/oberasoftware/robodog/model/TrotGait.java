package com.oberasoftware.robodog.model;

import java.util.Map;

/**
 * A simple trot gait implementation.
 * In a trot gait, diagonal pairs of legs move together.
 */
public class TrotGait implements Gait {

    @Override
    public void step(Map<String, Leg> legs, Vector3D direction, double stepHeight, double groundHeight, double progress) {
        // Diagonal pairs: (FL, BR) and (FR, BL)
        // Pair 1 moves during progress 0.0 - 0.5
        // Pair 2 moves during progress 0.5 - 1.0

        if (progress < 0.5) {
            double pairProgress = progress * 2.0;
            movePair(legs, "FL", "BR", direction, stepHeight, groundHeight, pairProgress);
            stayPut(legs, "FR", "BL", direction, groundHeight, pairProgress, false);
        } else {
            double pairProgress = (progress - 0.5) * 2.0;
            stayPut(legs, "FL", "BR", direction, groundHeight, pairProgress, true);
            movePair(legs, "FR", "BL", direction, stepHeight, groundHeight, pairProgress);
        }
    }

    private void movePair(Map<String, Leg> legs, String leg1, String leg2, Vector3D direction, double stepHeight, double groundHeight, double progress) {
        double x = -direction.getX() / 2.0 + direction.getX() * progress;
        double y = -direction.getY() / 2.0 + direction.getY() * progress;
        // Parabolic arc for height
        double z = groundHeight - Math.sin(progress * Math.PI) * stepHeight;

        moveLeg(legs, leg1, x, y, z);
        moveLeg(legs, leg2, x, y, z);
    }

    private void stayPut(Map<String, Leg> legs, String leg1, String leg2, Vector3D direction, double groundHeight, double progress, boolean firstPair) {
        // While the other pair is moving, this pair moves backwards relative to the body to maintain ground contact and move the body forward
        // If firstPair is true, it means we are in the second half of the gait (progress 0.5-1.0 for the whole gait)
        // so this pair (FL, BR) which moved in the first half is now pushing back.
        
        double x, y;
        if (firstPair) {
            // Pushing back from direction.getX()/2.0 to -direction.getX()/2.0
            x = direction.getX() / 2.0 - direction.getX() * progress;
            y = direction.getY() / 2.0 - direction.getY() * progress;
        } else {
            // This pair (FR, BL) will move in the second half, so it is currently pushing back from 0 to -direction.getX()/2.0? 
            // Actually, it should have finished its swing at 0 (or direction.getX()/2.0)
            // Let's simplify: the leg on the ground moves from +step/2 to -step/2
            x = direction.getX() / 2.0 - direction.getX() * progress;
            y = direction.getY() / 2.0 - direction.getY() * progress;
        }

        moveLeg(legs, leg1, x, y, groundHeight);
        moveLeg(legs, leg2, x, y, groundHeight);
    }

    private void moveLeg(Map<String, Leg> legs, String name, double x, double y, double z) {
        Leg leg = legs.get(name);
        if (leg != null) {
            leg.moveFootTo(new Vector3D(x, y, z));
        }
    }
}
