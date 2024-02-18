package com.oberasoftware.robo.maximus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.iot.core.robotics.humanoid.HumanoidRobot;
import com.oberasoftware.iot.core.robotics.servo.DynamixelDevice;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.DynamixelTorgueManager;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;
import com.oberasoftware.robo.maximus.motion.NavigationControlImpl;
import com.oberasoftware.robo.maximus.motion.cartesian.CartesianControlImpl;
import com.oberasoftware.robo.maximus.motion.cartesian.CoordinatesMonitor;
import com.oberasoftware.robo.maximus.sensors.Ina260CurrentSensor;
import com.oberasoftware.robo.maximus.sensors.LSM9DS1GyroSensor;
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

import static com.oberasoftware.iot.core.robotics.humanoid.components.ComponentNames.*;
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

    public static final List<String> SERVO_IDS = Lists.newArrayList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "120", "121", "122", "123", "124", "130", "131", "132", "133", "134", "141", "140");
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
//                .capability(TeensySensorDriver.class)
//                .capability(InfluxDBMetricsCapability.class)
                .capability(DynamixelTorgueManager.class)
//                .remote(RemoteCloudDriver.class, true)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        Set<String> detectedServos = robot.getServoDriver().getServos().stream()
                .map(DynamixelDevice::getId).collect(Collectors.toSet());

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
                        createLeg(RIGHT_LEG)
                                .ankle(RIGHT_ANKLE,
                                        create("110", RIGHT_ANKLE_PITCH),
                                        create("111", RIGHT_ANKLE_ROLL))
                                .knee(create("109", RIGHT_KNEE))
                                .hip(RIGHT_HIP,
                                        create("100", RIGHT_HIP_ROLL),
                                        create("102", RIGHT_HIP_PITCH),
                                        create("101", RIGHT_HIP_YAW)),
                        createLeg(LEFT_LEG)
                                .ankle(LEFT_ANKLE,
                                        create("104", LEFT_ANKLE_PITCH),
                                        create("105", LEFT_ANKLE_ROLL))
                                .knee(create("103", LEFT_KNEE, true ))
                                .hip(LEFT_HIP,
                                        create("106", LEFT_HIP_ROLL),
                                        create("108", LEFT_HIP_PITCH),
                                        create("107", LEFT_HIP_YAW)))
                .torso(
                        createArm(LEFT_ARM)
                                .shoulder(LEFT_SHOULDER,
                                        create("131", LEFT_SHOULDER_ROLL, true),
                                        create("130", LEFT_SHOULDER_PITCH))
                                .elbow(
                                        create("133", LEFT_ELBOW, true  ).max(110).min(-110),
                                        create("132", LEFT_ELBOW_ROLL))
                                .hand(create("134", LEFT_HAND).min(-5).max(20)), //"LeftHand", "134"
                        createArm(RIGHT_ARM)
                                .shoulder(RIGHT_SHOULDER,
                                        create("121", RIGHT_SHOULDER_ROLL, true),
                                        create("120", RIGHT_SHOULDER_PITCH, true)
                                        )
                                .elbow(
                                        create("123", RIGHT_ELBOW, true).max(110).min(-110),
                                        create("122", RIGHT_ELBOW_ROLL)
                                )
                                .hand(create("124", RIGHT_HAND, true).min(-5).max(20)))  //"RightHand", "124"
                .head("head",
                        create("141", PITCH_HEAD).min(-25).max(25),
                        create("140", ROLL_HEAD).min(-50).max(50))
                .sensor(new Ina260CurrentSensor())
                .sensor(new LSM9DS1GyroSensor())
                .behaviourController(new CartesianControlImpl())
                .behaviourController(new NavigationControlImpl())
                .behaviourController(new CoordinatesMonitor())
                .build();
        behaviouralRobotRegistry.register(maximus);

        robot.listen(new LowVoltageMonitor());

        return maximus;
    }

    public static class LowVoltageMonitor implements EventHandler {
        @EventSubscribe
        public void receive(SensorDataImpl data) {
            if(data.getSourcePath().equalsIgnoreCase("ina260.voltage")) {
                Double v = data.getValue("voltage");
                if(v < 11.6) {
                    LOG.error("Low battery voltage: {}, Exiting robot", v);
//                    System.exit(-1);
                }
            }
        }
    }
}
