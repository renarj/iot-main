package com.oberasoftware.robo.maximus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;
import com.oberasoftware.robo.api.servo.ServoDriver;
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

import java.util.List;

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

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Value("${robot.port:}")
    private String dynamixelPort;

    public void initialize() {
        List<String> servos = Lists.newArrayList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "120", "121", "122", "123", "124", "130", "131", "132", "133", "134", "141", "140");
        String motorIdentifiers = String.join(",", servos);

        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        Robot robot = new SpringAwareRobotBuilder("maximus-core", applicationContext)
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort)
                                .put("motors", motorIdentifiers)
                                .build())
//                .servoDriver(new MockServoDriver(servos), new HashMap<>())
                .capability(ServoSensorDriver.class)
                .capability(MotionStorage.class)
                .capability(TeensySensorDriver.class)
//                .remote(RemoteCloudDriver.class)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        ServoDriver driver = robot.getServoDriver();

//        driver.sendCommand(new RebootCommand("107"));
//        driver.getServos().forEach(s -> driver.sendCommand(new CurrentLimitCommand(s.getId(), 50)));

//        robot.getServoDriver().sendCommand(new VelocityModeCommand("133", 20, 20));
//        driver.getServos().forEach(s -> driver.sendCommand(new VelocityModeCommand(s.getId(), 50, 5)));

//        ServoDriver servoDriver = robot.getServoDriver();
//        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), false));
//        servoDriver.getServos().forEach(s -> servoDriver.sendCommand(new OperationModeCommand(s.getId(), POSITION_CONTROL)));
//        servoDriver.getServos().forEach(s -> servoDriver.setTorgue(s.getId(), true));

        HumanoidRobot maximus = HumanoidRobotBuilder.create(robot, "maximus")
                .legs(
                    createLeg("LeftLeg")
                        .ankle("leftAnkle",
                                create("104", "leftAnkle-x"),
                                create("105", "leftAnkle-y"))
                        .knee(create("103", "LeftKnee").max(5).min(-110))
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
                                .shoulder("leftShoulder","131", "130", "132")
                                .elbow(create("133", "LeftElbow", true  ).max(110).min(-110))
                                .hand("LeftHand", "134"),
                        createArm("RightArm")
                                .shoulder("rightShoulder", "121", "120", "122")
                                .elbow(create("123", "RightElbow").max(110).min(-110))
                                .hand("RightHand", "124"))
                .head("head", "141", "140")
                .sensor(new Ina260CurrentSensor())
                .sensor(new LSM9DS1GyroSensor())
            .build();
        behaviouralRobotRegistry.register(maximus);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
