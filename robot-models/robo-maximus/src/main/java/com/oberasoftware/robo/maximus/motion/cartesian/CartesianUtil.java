package com.oberasoftware.robo.maximus.motion.cartesian;

public class CartesianUtil {
    private static final double POW2 = 2.0;
    private static final int MULIP2 = 2;

    public static double lawOfSinesTwoAngles(double side, double angleA, double angleB) {
        return (side * Math.sin(Math.toRadians(angleA))) / Math.sin(Math.toRadians(angleB));
    }

    public static double lawOfSinesTwoSides(double sideA, double sideB, double angle) {
        return Math.toDegrees(Math.asin((Math.sin(Math.toRadians(angle)) * sideA) / sideB));
    }

    public static double lawOfCosines(double sideA, double sideB, double sideC) {
        double b2c2a2 = Math.pow(sideC, 2) + Math.pow(sideA, 2) - Math.pow(sideB, 2);
        double bc2 = MULIP2 * sideC * sideA;

        return Math.toDegrees(Math.acos(b2c2a2 / bc2));
    }

    public static double lawOfCosinesWithAngle(double sideA, double sideB, double angle) {
        double b2c2 = (Math.pow(sideA, POW2) + Math.pow(sideB, POW2));
        double bc2 =  (MULIP2 * sideA * sideB);
        double cos = Math.cos(Math.toRadians(angle));

        return Math.sqrt(b2c2 - (bc2 * cos));
    }
}
