package com.oberasoftware.robo.maximus.drive;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveBehaviour;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.navigation.DirectionalInput;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class MecanumDriveTrainImpl implements DriveBehaviour {
    private static final Logger LOG = getLogger(MecanumDriveTrainImpl.class);

    private final String frontLeft;
    private final String frontRight;
    private final String rearLeft;
    private final String rearRight;

    private ServoDriver servoDriver;

    private Map<Integer, Wheel> wheels = new HashMap<>();

    private static final int LEFT_FRONT = 0;
    private static final int RIGHT_FRONT = 1;
    private static final int LEFT_REAR = 2;
    private static final int RIGHT_REAR = 3;

    private static final double OUTPUT_SCALE_FACTOR = 100.0;

    public MecanumDriveTrainImpl(String frontLeft, String frontRight, String rearLeft, String rearRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robot) {
        this.servoDriver = robot.getServoDriver();
        wheels.put(LEFT_FRONT, behaviouralRobot.getWheels(frontLeft));
        wheels.put(LEFT_REAR, behaviouralRobot.getWheels(rearLeft));
        wheels.put(RIGHT_FRONT, behaviouralRobot.getWheels(frontRight));
        wheels.put(RIGHT_REAR, behaviouralRobot.getWheels(rearRight));
    }


    @Override
    public void forward(int speed, Scale scale) {
        double convertedSpeed = convert(-1.0, speed, scale);

        cartesian(0, convertedSpeed, 0);
    }

    @Override
    public void backward(int speed, Scale scale) {
        double convertedSpeed = convert(1.0, speed, scale);

        cartesian(0, convertedSpeed, 0);
    }

    @Override
    public void drive(DirectionalInput input, Scale scale) {
        try {
            double x = input.hasInputAxis("x") ? input.getAxis("x") : 0.0;
            double y = input.hasInputAxis("y") ? input.getAxis("y") : 0.0;
            double z = input.hasInputAxis("z") ? input.getAxis("z") : 0.0;

            double cX = convert(1.0, x, scale);
            double cY = convert(1.0, y, scale);
            double cZ = convert(1.0, z, scale);

            cartesian(cX, cY, cZ);
        } catch(Exception e) {
            LOG.error("", e);
        }
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
        LOG.info("Setting cartesian drive x: {} y: {} rotation: {}", x, y, rotation);
        double xIn = x;
        double yIn = y;

        // Compensate for gyro angle.
//        double[] rotated = rotateVector(xIn, yIn, 0.0);
//        xIn = rotated[0];
//        yIn = rotated[1];

        double[] wheelSpeeds = new double[4];
        wheelSpeeds[LEFT_FRONT] = xIn + yIn + rotation;
        wheelSpeeds[RIGHT_FRONT] = -xIn + yIn - rotation;
        wheelSpeeds[LEFT_REAR] = -xIn + yIn + rotation;
        wheelSpeeds[RIGHT_REAR] = xIn + yIn - rotation;
        LOG.info("Wheel speeds: {}", wheelSpeeds);

        normalize(wheelSpeeds);
        scale(wheelSpeeds, OUTPUT_SCALE_FACTOR);
        LOG.info("Scaled and normalized wheel speeds: {}", wheelSpeeds);

        LOG.info("Front Left {} Front Right {} Rear Left {} Rear Right {}", wheelSpeeds[LEFT_FRONT], wheelSpeeds[RIGHT_FRONT], wheelSpeeds[LEFT_REAR], wheelSpeeds[RIGHT_REAR]);
        setWheelSpeeds(wheelSpeeds);
    }

    private void setWheelSpeeds(double[] wheelSpeeds) {
        for(int i=0; i<wheels.size(); i++) {
            int speed = (int) wheelSpeeds[i];
            Wheel wheel = wheels.get(i);

            if(wheel.isReversed()) {
                speed = -speed;
            }
            servoDriver.setServoSpeed(wheel.getServoId(), speed, new Scale(-100, 100));
//            if(speed < 0) {
//                wheel.backward(speed);
//            } else {
//                wheel.forward(speed);
//            }
        }
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
        double[] out = new double[2];
        out[0] = x * cosA - y * sinA;
        out[1] = x * sinA + y * cosA;
        return out;
    }

        /**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     * @param wheelSpeeds the speed of each motor
     */
    protected static void normalize(double[] wheelSpeeds) {
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
    protected static void scale(double[] wheelSpeeds, double scaleFactor) {
        for (int i = 0; i < 4; i++) {
            wheelSpeeds[i] = wheelSpeeds[i] * scaleFactor;
        }
    }

    @Override
    public void left(int speed, Scale scale) {
        double convertedSpeed = convert(-1.0, speed, scale);

        cartesian(convertedSpeed, 0, 0);
    }

    @Override
    public void right(int speed, Scale scale) {
        double convertedSpeed = convert(1.0, speed, scale);

        cartesian(convertedSpeed, 0, 0);
    }

    @Override
    public void stop() {
//        wheels.forEach(Wheel::stop);
    }

    private double convert(double max, double speed, Scale scale) {
        double factor = max / (double) scale.getMax();

        return speed * factor;
    }
}
