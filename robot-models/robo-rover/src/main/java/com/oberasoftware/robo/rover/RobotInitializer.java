package com.oberasoftware.robo.rover;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.JointBasedRobotRegistery;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.core.behaviours.BehaviouralRobotBuilder;
import com.oberasoftware.robo.core.behaviours.WheelBasedWithCameraNavigationControllerImpl;
import com.oberasoftware.robo.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.robo.core.behaviours.wheels.impl.MecanumDriveTrainImpl;
import com.oberasoftware.robo.core.behaviours.wheels.impl.WheelImpl;
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

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private JointBasedRobotRegistery jointBasedRobotRegistery;

    @Value("${dynamixelPort:}")
    private String dynamixelPort;

    public void initialize() {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        RobotHardware robot = new HardwareRobotBuilder("roverpi", applicationContext)
//                .motionEngine(RoboPlusMotionEngine.class,
//                        new JsonMotionResource("/basic-animations.json")
//                )
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort).build())
                .capability(ServoSensorDriver.class)
//                .remote(RemoteCloudDriver.class)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        ServoDriver servoDriver = robot.getServoDriver();
        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), false));
        Sets.newHashSet("19", "20", "21", "22").forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s, OperationModeCommand.MODE.VELOCITY_MODE)));
        Sets.newHashSet("23", "24").forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s, OperationModeCommand.MODE.POSITION_CONTROL)));
        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), true));

        Wheel frontLeft = new WheelImpl("22", false);
        Wheel frontRight = new WheelImpl("21", true);
        Wheel rearLeft = new WheelImpl("19", false);
        Wheel rearRight = new WheelImpl("20", true);

        SingleServoBehaviour camerRotate = new SingleServoBehaviour("23", 1350, 650, 1000);
        SingleServoBehaviour cameraTilt = new SingleServoBehaviour("24", 1266, 600, 1000);
        MecanumDriveTrainImpl mecanumDriveTrain = new MecanumDriveTrainImpl(frontLeft, frontRight, rearLeft, rearRight);

        Robot robotCar = BehaviouralRobotBuilder.create(robot)
                .camera(cameraTilt, camerRotate)
                .wheels(mecanumDriveTrain)
                .navigation(new WheelBasedWithCameraNavigationControllerImpl())
                .build("test");
        jointBasedRobotRegistery.register(robotCar);
        LOG.info("Robot: {} was registered", robotCar);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
