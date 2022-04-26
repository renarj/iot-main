package com.oberasoftware.robo.container;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.oberasoftware.max.core.BehaviouralRobotBuilder;
import com.oberasoftware.max.core.behaviours.WheelBasedWithCameraNavigationControllerImpl;
import com.oberasoftware.max.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.impl.MecanumDriveTrainImpl;
import com.oberasoftware.max.core.behaviours.wheels.impl.WheelImpl;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.wheel.Wheel;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.cloud.RemoteCloudDriver;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.commands.OperationModeCommand;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class RobotInitializer {
    private static final Logger LOG = getLogger(RobotInitializer.class);

    private static final Scale DEFAULT_SCALE = new Scale(-100, 100);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Value("${dynamixelPort:}")
    private String dynamixelPort;

    public void initialize() {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        Robot robot = new SpringAwareRobotBuilder("max", applicationContext)
//                .motionEngine(RoboPlusMotionEngine.class,
//                        new JsonMotionResource("/basic-animations.json")
//                )
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort).build())
                .capability(ServoSensorDriver.class)
                .remote(RemoteCloudDriver.class)
                .build();
//        robot.getServoDriver().sendCommand(new DynamixelAngleLimitCommand("5", DynamixelAngleLimitCommand.MODE.WHEEL_MODE));
//        robot.getServoDriver().sendCommand(new DynamixelAngleLimitCommand("9", DynamixelAngleLimitCommand.MODE.WHEEL_MODE));
//        robot.getServoDriver().sendCommand(new DynamixelAngleLimitCommand("6", DynamixelAngleLimitCommand.MODE.WHEEL_MODE));
//        robot.getServoDriver().sendCommand(new DynamixelAngleLimitCommand("16", DynamixelAngleLimitCommand.MODE.WHEEL_MODE));

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        ServoDriver servoDriver = robot.getServoDriver();
//        servoDriver.getServos().forEach(s -> servoDriver.sendCommand(new AngleLimitCommand(s.getId(), AngleLimitCommand.MODE.JOINT_MODE)));
        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), false));

        Sets.newHashSet("19", "20", "21", "22").forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s, OperationModeCommand.MODE.VELOCITY_MODE)));
        Sets.newHashSet("23", "24").forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s, OperationModeCommand.MODE.POSITION_CONTROL)));

        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), true));

//        WheelAction forwardAction = (si, s, d) -> d.setServoSpeed(si, s, new Scale(-100, 100));
//        WheelAction backwardAction = (si, s, d) -> d.setServoSpeed(si, s + 1024, new Scale(-100, 100));
//
//        DriveTrain left = new SimpleDriveTrainImpl(newArrayList(
//                new WheelImpl("5", true, forwardAction, backwardAction),
//                new WheelImpl("9", true, forwardAction, backwardAction)));
//        DriveTrain right = new SimpleDriveTrainImpl(newArrayList(
//                new WheelImpl("16", false, forwardAction, backwardAction),
//                new WheelImpl("6", false, forwardAction, backwardAction)));
//
        Wheel frontLeft = new WheelImpl("22", false);
        Wheel frontRight = new WheelImpl("21", true);
        Wheel rearLeft = new WheelImpl("19", false);
        Wheel rearRight = new WheelImpl("20", true);

        SingleServoBehaviour camerRotate = new SingleServoBehaviour("23", 1350, 650, 1000);
        SingleServoBehaviour cameraTilt = new SingleServoBehaviour("24", 1266, 600, 1000);
//
        MecanumDriveTrainImpl mecanumDriveTrain = new MecanumDriveTrainImpl(frontLeft, frontRight, rearLeft, rearRight);
//
        BehaviouralRobot robotCar = BehaviouralRobotBuilder.create(robot)
//                .gripper(GripperBuilder.create(
//                        new SingleServoBehaviour("3", 640, 420, 640),
//                        new SingleServoBehaviour("4", 426, 570, 582))
//                        .rotation(new SingleServoBehaviour("10", 350, 650, 512))
//                        .elevator(new SingleServoBehaviour("14", 750, 600, 750)))
//                .wheels(left, right)
                .camera(cameraTilt, camerRotate)
                .wheels(mecanumDriveTrain)
                .navigation(new WheelBasedWithCameraNavigationControllerImpl())
                .build();
        behaviouralRobotRegistry.register(robotCar);
        LOG.info("Robot: {} was registered", robotCar);
//
//        LOG.info("Starting wheels forward");
//        robotCar.getWheels().ifPresent(w -> w.forward(10, DEFAULT_SCALE));
//        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
//        robotCar.getWheels().ifPresent(w -> w.backward(10, DEFAULT_SCALE));
////
//
//        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
//        robotCar.getWheels().ifPresent(w -> w.left(10, DEFAULT_SCALE));
//        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
//        robotCar.getWheels().ifPresent(w -> w.right(10, DEFAULT_SCALE));
//
//        //working strafing
//        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
//        robot.getServoDriver().setServoSpeed("6", 900);
//        robot.getServoDriver().setServoSpeed("9", 1900);
//        robot.getServoDriver().setServoSpeed("16", 1900);
//        robot.getServoDriver().setServoSpeed("5", 900);
//        mecanumDriveTrain.forward(512);
//        mecanumDriveTrain.cartesian(0, -0.2, 0);
//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
//
//        mecanumDriveTrain.cartesian(0, -0.5, -0.2);
//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
//
//        mecanumDriveTrain.cartesian(0, -0.5, 0.2);
//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);



//        mecanumDriveTrain.backward(512);
//        mecanumDriveTrain.cartesian(-0.5, -0.25, 0);
//        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
//
//        mecanumDriveTrain.left(512);
//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
//
//        mecanumDriveTrain.right(512);
//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

//        for(int i=0; i<360; i = i + 40) {
//            mecanumDriveTrain.cartesian(0, -0.5, i);
//            Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
//        }

//        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

//
//        robotCar.getWheels().ifPresent(DriveBehaviour::stop);
//        LOG.info("Killed wheels");
//
//        System.exit(0);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
