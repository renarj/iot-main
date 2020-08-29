package com.oberasoftware.robo.maximus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.maximus.sensors.Ina260CurrentSensor;
import com.oberasoftware.robo.maximus.sensors.LSM9DS1GyroSensor;
import com.oberasoftware.robo.maximus.sensors.TeensySensorDriver;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.oberasoftware.robo.maximus.HumanoidRobotBuilder.ArmBuilder.createArm;
import static com.oberasoftware.robo.maximus.HumanoidRobotBuilder.JointBuilder.create;
import static com.oberasoftware.robo.maximus.HumanoidRobotBuilder.LegBuilder.createLeg;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class RobotInitializer {
    private static final Logger LOG = getLogger(RobotInitializer.class);

    public static final List<String> SERVO_IDS = Lists.newArrayList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "120", "121", "123", "124", "130", "131", "133", "134", "141", "140");
    private static final String MOTOR_ID_STRING = String.join(",", SERVO_IDS);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Value("${robot.port:}")
    private String dynamixelPort;

    public void initialize() {
        initialize(new ArrayList<>(), false);
    }

    public void initialize(BiConsumer<Robot, HumanoidRobot> action, boolean terminateAfterAction) {
        initialize(Lists.newArrayList(action), terminateAfterAction);

    }

    public void initialize(List<BiConsumer<Robot, HumanoidRobot>> actions, boolean terminateAfterAction) {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        Robot robot = new SpringAwareRobotBuilder("maximus-core", applicationContext)
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort)
                                .put("motors", MOTOR_ID_STRING)
                                .build())
                .capability(ServoSensorDriver.class)
                .capability(MotionStorage.class)
                .capability(TeensySensorDriver.class)
//                .capability(InfluxDBMetricsCapability.class)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        Set<String> detectedServos = robot.getServoDriver().getServos().stream().map(s -> s.getId()).collect(Collectors.toSet());

        Set<String> undetectedServos = new HashSet<>(SERVO_IDS);
        undetectedServos.removeAll(detectedServos);
        undetectedServos.forEach(s -> LOG.info("Did not detect servo: {}", s));

        HumanoidRobot humanoidRobot = constructHumanoid(robot);
        actions.forEach(a -> {
            a.accept(robot, humanoidRobot);
        });

        if(terminateAfterAction) {
            Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);

            robot.shutdown();
            System.exit(-1);
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Killing the robot gracefully on shutdown");
                robot.shutdown();
            }));
        }
    }

    private HumanoidRobot constructHumanoid(Robot robot) {
        HumanoidRobot maximus = HumanoidRobotBuilder.create(robot, "maximus")
                .legs(
                        createLeg("LeftLeg")
                                .ankle("leftAnkle",
                                        create("104", "leftAnkle-x"),
                                        create("105", "leftAnkle-y"))
                                .knee(create("103", "LeftKnee", true).max(110).min(-5))
                                .hip("leftHip",
                                        create("100", "leftHip-x"),
                                        create("102", "leftHip-y"),
                                        create("101", "leftHip-z")),
                        createLeg("RightLeg")
                                .ankle("rightAnkle",
                                        create("110", "rightAnkle-x"),
                                        create("111", "rightAnkle-y"))
                                .knee(create("109", "RightKnee").max(110).min(-5))
                                .hip("rightHip",
                                        create("106", "rightHip-x"),
                                        create("108", "rightHip-y"),
                                        create("107", "rightHip-z")))
                .torso(
                        createArm("LeftArm")
                                .shoulder("leftShoulder","131", "130")
                                .elbow(create("133", "LeftElbow", false  ).max(110).min(-110))
                                .hand("LeftHand", "134"),
                        createArm("RightArm")
                                .shoulder("rightShoulder",
                                        create("121", "rightShoulderX", true),
                                        create("120", "rightShoulderY", true)
                                        )
//                                .shoulder("rightShoulder", "121", "120", "122")
                                .elbow(create("123", "RightElbow", true).max(110).min(-110))
                                .hand("RightHand", "124"))
                .head("head", "141", "140")
                .sensor(new Ina260CurrentSensor())
                .sensor(new LSM9DS1GyroSensor())
                .build();
        behaviouralRobotRegistry.register(maximus);

        return maximus;
    }
}
