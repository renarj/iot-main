package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.max.core.behaviours.wheels.DriveBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.Wheel;
import com.oberasoftware.robo.api.Robot;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class MecanumDriveTrainImpl implements DriveBehaviour {
    private static final Logger LOG = getLogger(MecanumDriveTrainImpl.class);

    private final List<Wheel> wheels;

    private static final int LEFT_FRONT = 0;
    private static final int RIGHT_FRONT = 1;
    private static final int LEFT_REAR = 2;
    private static final int RIGHT_REAR = 3;

    private static final double SQRT_OF_TWO = Math.sqrt(2.0);
    private static final double OUTPUT_SCALE_FACTOR = 100;
    public static final double DEFAULT_MINIMUM_SPEED = 0.02;
    public static final double DEFAULT_MAXIMUM_SPEED = 1.0;


    public MecanumDriveTrainImpl(Wheel leftFront, Wheel rightFront, Wheel leftBack, Wheel rightBack) {
        wheels = newArrayList(leftFront, rightFront, leftBack, rightBack);
    }

    @Override
    public void forward(int speed) {
        cartesian(0, -1, 0);
    }

    @Override
    public void backward(int speed) {
        cartesian(0, 1, 0);
    }

    /**
     * Cartesian drive method that specifies speeds in terms of the field longitudinal and lateral directions, using the drive's
     * angle sensor to automatically determine the robot's orientation relative to the field.
     * <p>
     * Using this method, the robot will move away from the drivers when the joystick is pushed forwards, and towards the
     * drivers when it is pulled towards them - regardless of what direction the robot is facing.
     *
     * @param x The speed that the robot should drive in the X direction. [-1.0..1.0]
     * @param y The speed that the robot should drive in the Y direction. This input is inverted to match the forward == -1.0
     *        that joysticks produce. [-1.0..1.0]
     * @param rotation The rate of rotation for the robot that is completely independent of the translation. [-1.0..1.0]
     */
    public void cartesian(double x, double y, double rotation) {
        double xIn = x;
        double yIn = y;
        // Negate y for the joystick.
        yIn = -yIn;
        // Compensate for gyro angle.
        double rotated[] = rotateVector(xIn, yIn, 0.0);
        xIn = rotated[0];
        yIn = rotated[1];

        double wheelSpeeds[] = new double[4];
        wheelSpeeds[LEFT_FRONT] = xIn + yIn + rotation;
        wheelSpeeds[RIGHT_FRONT] = -xIn + yIn - rotation;
        wheelSpeeds[LEFT_REAR] = -xIn + yIn + rotation;
        wheelSpeeds[RIGHT_REAR] = xIn + yIn - rotation;

        normalize(wheelSpeeds);
        scale(wheelSpeeds, OUTPUT_SCALE_FACTOR);

        LOG.info("Front Left {} Front Right {} Rear Left {} Rear Right {}", wheelSpeeds[LEFT_FRONT], wheelSpeeds[RIGHT_FRONT], wheelSpeeds[LEFT_REAR], wheelSpeeds[RIGHT_REAR]);
        setWheelSpeeds(wheelSpeeds);
    }

    private void setWheelSpeeds(double[] wheelSpeeds) {
        for(int i=0; i<wheels.size(); i++) {
            int speed = (int) wheelSpeeds[i];
            Wheel wheel = wheels.get(i);

            if(speed < 0) {
                wheel.backward(speed);
            } else {
                wheel.forward(speed);
            }
        }
    }

    /**
     * Polar drive method that specifies speeds in terms of magnitude and direction. This method does not use the drive's angle
     * sensor.
     *
     * @param magnitude The speed that the robot should drive in a given direction.
     * @param direction The direction the robot should drive in degrees. The direction and magnitude are independent of the
     *        rotation rate.
     * @param rotation The rate of rotation for the robot that is completely independent of the magnitude or direction.
     *        [-1.0..1.0]
     */
    public void polar(double magnitude, double direction, double rotation) {
        // Normalized for full power along the Cartesian axes.
//        magnitude = speedLimiter.applyAsDouble(magnitude) * SQRT_OF_TWO;
        magnitude = symmetricLimit(DEFAULT_MINIMUM_SPEED, magnitude, DEFAULT_MAXIMUM_SPEED) * SQRT_OF_TWO;

        // The rollers are at 45 degree angles.
        double dirInRad = (direction + 45.0) * Math.PI / 180.0;
        double cosD = Math.cos(dirInRad);
        double sinD = Math.sin(dirInRad);

        double wheelSpeeds[] = new double[4];
        wheelSpeeds[LEFT_FRONT] = (sinD * magnitude + rotation);
        wheelSpeeds[RIGHT_FRONT] = (cosD * magnitude - rotation);
        wheelSpeeds[LEFT_REAR] = (cosD * magnitude + rotation);
        wheelSpeeds[RIGHT_REAR] = (sinD * magnitude - rotation);

        normalize(wheelSpeeds);
        scale(wheelSpeeds, OUTPUT_SCALE_FACTOR);

        setWheelSpeeds(wheelSpeeds);
    }

    public static double symmetricLimit(double minimum, double num, double maximum) {
        if ( minimum < 0 ) throw new IllegalArgumentException("The minimum value may not be negative");
        if ( maximum < 0 ) throw new IllegalArgumentException("The maximum value may not be negative");
        if ( maximum < minimum ) throw new IllegalArgumentException("The minimum value must be less than or equal to the maximum value");
        if (num > maximum) {
            return maximum;
        }
        double positiveNum = Math.abs(num);
        if (positiveNum > maximum) {
            return -maximum;
        }
        return positiveNum > minimum ? num : 0.0;
    }

    /**
     * Rotate a vector in Cartesian space.
     * @param x the x value of the vector
     * @param y the y value of the vector
     * @param angle the angle to rotate
     * @return the vector of x and y values
     */
    protected static double[] rotateVector(double x, double y, double angle) {
        double angleInRadians = Math.toRadians(angle);
        double cosA = Math.cos(angleInRadians);
        double sinA = Math.sin(angleInRadians);
        double out[] = new double[2];
        out[0] = x * cosA - y * sinA;
        out[1] = x * sinA + y * cosA;
        return out;
    }

        /**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     * @param wheelSpeeds the speed of each motor
     */
    protected static void normalize(double wheelSpeeds[]) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        for (int i = 1; i < 4; i++) {
            double temp = Math.abs(wheelSpeeds[i]);
            if (maxMagnitude < temp) maxMagnitude = temp;
        }
        if (maxMagnitude > 1.0) {
            for (int i = 0; i < 4; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
            }
        }
    }

    /**
     * Scale all speeds.
     * @param wheelSpeeds the speed of each motor
     * @param scaleFactor the scale factor to apply to the motor speeds
     */
    protected static void scale(double wheelSpeeds[], double scaleFactor) {
        for (int i = 0; i < 4; i++) {
            wheelSpeeds[i] = wheelSpeeds[i] * scaleFactor;
        }
    }

    @Override
    public void drive(int speed, DIRECTION direction) {

    }

    @Override
    public void left(int speed) {
        cartesian(-1, 0, 0);
    }

    @Override
    public void right(int speed) {
        cartesian(1, 0, 0);
    }

    @Override
    public void stop() {
        wheels.forEach(Wheel::stop);
    }

    @Override
    public List<Wheel> getWheels() {
        return null;
    }

    @Override
    public void initialize(Robot robot) {
        wheels.forEach(w -> w.initialize(robot));
    }
}
