package com.oberasoftware.robo.maximus;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.cloud.RemoteCloudDriver;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.commands.OperationModeCommand;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.motion.JsonMotionResource;
import com.oberasoftware.robo.dynamixel.motion.RoboPlusMotionEngine;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.oberasoftware.robo.core.commands.OperationModeCommand.MODE.POSITION_CONTROL;
import static com.oberasoftware.robo.maximus.HumanoidRobotBuilder.LegBuilder.create;
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
    private BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Value("${dynamixelPort:}")
    private String dynamixelPort;

    public void initialize() {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        Robot robot = new SpringAwareRobotBuilder("maximus", applicationContext)
                .motionEngine(RoboPlusMotionEngine.class,
                        new JsonMotionResource("/basic-animations.json")
                )
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort).build())
                .capability(ServoSensorDriver.class)
                .remote(RemoteCloudDriver.class)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        ServoDriver servoDriver = robot.getServoDriver();
        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), false));
        servoDriver.getServos().forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s.getId(), POSITION_CONTROL)));
        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), true));

        HumanoidRobot maximus = HumanoidRobotBuilder.create("maximus")
                .legs(
                    create("LeftLeg")
                        .ankle("x-id", "y-id")
                        .knee("knee-id")
                        .hip("hip-x", "hip-y", "hip-z"),
                    create("RightLeg")
                            .ankle("x-id", "y-id")
                            .knee("knee-id")
                            .hip("hip-x", "hip-y", "hip-z"))
                .torso(
                        HumanoidRobotBuilder.ArmBuilder.create("LeftArm")
                                .shoulder("x-id", "y-id", "z-id")
                                .elbow("elbow-id")
                                .hand("hand-id"),
                        HumanoidRobotBuilder.ArmBuilder.create("RightArm")
                                .shoulder("x-id", "y-id", "z-id")
                                .elbow("elbow-id")
                                .hand("hand-id"))
                .head("pitchId", "yawId")
            .build();



//        Ankle leftAnkle = new Ankle(new Joint("Ankle-X", "104"), new Joint("Ankle-Y", "105"));
//        Hip leftHip = new Hip(new Joint("Hip-X", "100"), new Joint("Hip-Z", "101"), new Joint("Hip-Y", "102"));
//        Leg leftLeg = new Leg(leftHip, new Joint("Left-Knee", "103"), leftAnkle);
//
//        Ankle rightAnkle = new Ankle(new Joint("Ankle-X", "110"), new Joint("Ankle-Y", "111"));
//        Hip rightHip = new Hip(new Joint("Hip-X", "106"), new Joint("Hip-Z", "107"), new Joint("Hip-Y", "108"));
//        Leg rightLeg = new Leg(rightHip, new Joint("Right-Knee", "109"), rightAnkle);
//
//
//        Legs legs = new Legs(leftLeg, rightLeg);
//
//        new Humanoid(head, torso, arms, legs);
//
//        BehaviouralRobot robotCar = BehaviouralRobotBuilder.create(robot)
//                .camera(cameraTilt, camerRotate)
//                .wheels(mecanumDriveTrain)
//                .navigation(new WheelBasedWithCameraNavigationControllerImpl())
//                .build();
//        behaviouralRobotRegistry.register(robotCar);
//        LOG.info("Robot: {} was registered", robotCar);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
