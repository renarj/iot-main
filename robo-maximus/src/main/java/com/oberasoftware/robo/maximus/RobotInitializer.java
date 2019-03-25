package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.dynamixel.motion.JsonMotionResource;
import com.oberasoftware.robo.dynamixel.motion.RoboPlusMotionEngine;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
//                .servoDriver(DynamixelServoDriver.class,
//                        ImmutableMap.<String, String>builder()
//                                .put(DynamixelServoDriver.PORT, dynamixelPort).build())
//                .capability(ServoSensorDriver.class)
//                .remote(RemoteCloudDriver.class)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

//        ServoDriver servoDriver = robot.getServoDriver();
//        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), false));
//        servoDriver.getServos().forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s.getId(), POSITION_CONTROL)));
//        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), true));

        HumanoidRobot maximus = HumanoidRobotBuilder.create("maximus")
                .legs(
                    create("LeftLeg")
                        .ankle("leftAnkle","104", "105")
                        .knee("LeftKnee", "103")
                        .hip("leftHip","100", "102", "101"),
                    create("RightLeg")
                            .ankle("rightAnkle","110", "111")
                            .knee("RightKnee", "109")
                            .hip("rightHip","106", "108", "107"))
                .torso(
                        HumanoidRobotBuilder.ArmBuilder.create("LeftArm")
                                .shoulder("leftShoulder","131", "130", "132")
                                .elbow("LeftElbow", "133")
                                .hand("LeftHand", "124"),
                        HumanoidRobotBuilder.ArmBuilder.create("RightArm")
                                .shoulder("rightShoulder", "121", "120", "122")
                                .elbow("RightElbow", "123")
                                .hand("RightHand", "124"))
                .head("head", "141", "140")
            .build();
        behaviouralRobotRegistry.register(maximus);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
