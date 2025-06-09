package com.oberasoftware.robo.maximus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.ConfiguredRobotRegistery;
import com.oberasoftware.iot.core.robotics.humanoid.ConfigurableRobot;
import com.oberasoftware.iot.core.robotics.servo.DynamixelDevice;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.DynamixelStateManager;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;
import com.oberasoftware.robo.maximus.motion.NavigationControlImpl;
import com.oberasoftware.robo.maximus.motion.cartesian.CartesianControlImpl;
import com.oberasoftware.robo.maximus.motion.cartesian.CoordinatesMonitor;
import com.oberasoftware.robo.maximus.sensors.Ina260CurrentSensor;
import com.oberasoftware.robo.maximus.sensors.LSM9DS1GyroSensor;
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
import static com.oberasoftware.robo.maximus.ConfigurableRobotBuilder.ArmBuilder.createArm;
import static com.oberasoftware.robo.maximus.ConfigurableRobotBuilder.JointBuilder.create;
import static com.oberasoftware.robo.maximus.ConfigurableRobotBuilder.LegBuilder.createLeg;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class StaticRobotInitializer {
    private static final Logger LOG = getLogger(StaticRobotInitializer.class);

    public static final List<String> SERVO_IDS = Lists.newArrayList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "120", "121", "122", "123", "124", "130", "131", "132", "133", "134", "141", "140");
    private static final String MOTOR_ID_STRING = String.join(",", SERVO_IDS);


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private ConfiguredRobotRegistery configuredRobotRegistery;

    @Value("${robot.port:}")
    private String dynamixelPort;

    public void initialize() {
        initialize(new ArrayList<>(), false);
    }

    public void initialize(BiConsumer<RobotHardware, ConfigurableRobot> action, boolean terminateAfterAction) {
        initialize(Lists.newArrayList(action), terminateAfterAction);

    }

    public void initialize(List<BiConsumer<RobotHardware, ConfigurableRobot>> actions, boolean terminateAfterAction) {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        RobotHardware robot = new HardwareRobotBuilder("maximus-core", applicationContext)
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort)
                                .put("motors", MOTOR_ID_STRING)
                                .build())
                .capability(ServoSensorDriver.class)
//                .capability(MotionStorage.class)
//                .capability(TeensySensorDriver.class)
//                .capability(InfluxDBMetricsCapability.class)
                .capability(DynamixelStateManager.class)
//                .remote(RemoteCloudDriver.class, true)
                .build();

        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        Set<String> detectedServos = robot.getServoDriver().getServos().stream()
                .map(DynamixelDevice::getId).collect(Collectors.toSet());

        Set<String> undetectedServos = new HashSet<>(SERVO_IDS);
        undetectedServos.removeAll(detectedServos);
        undetectedServos.forEach(s -> LOG.info("Did not detect servo: {}", s));

        ConfigurableRobot configurableRobot = constructHumanoid(robot);
        actions.forEach(a -> {
            a.accept(robot, configurableRobot);
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

    private ConfigurableRobot constructHumanoid(RobotHardware robot) {
        ConfigurableRobot maximus = ConfigurableRobotBuilder.create("test", "maximus")
                .legs(
                        createLeg(RIGHT_LEG)
                                .ankle(RIGHT_ANKLE,
                                        create("105", RIGHT_ANKLE_ROLL),
                                        create("102", RIGHT_ANKLE_PITCH, true))
                                .knee(create("103", RIGHT_KNEE, true))
                                .hip(RIGHT_HIP,
                                        create("108", RIGHT_HIP_ROLL),
                                        create("104", RIGHT_HIP_PITCH),
                                        create("107", RIGHT_HIP_YAW)),
                        createLeg(LEFT_LEG)
                                .ankle(LEFT_ANKLE,
                                        create("111", LEFT_ANKLE_ROLL),
                                        create("110", LEFT_ANKLE_PITCH))
                                .knee(create("109", LEFT_KNEE, false ))
                                .hip(LEFT_HIP,
                                        create("106", LEFT_HIP_ROLL),
                                        create("100", LEFT_HIP_PITCH, true),
                                        create("132", LEFT_HIP_YAW)))
                .torso(
                        createArm(LEFT_ARM)
                                .shoulder(LEFT_SHOULDER,
                                        create("131", LEFT_SHOULDER_ROLL, true),
                                        create("120", LEFT_SHOULDER_PITCH))
                                .elbow(
                                        create("123", LEFT_ELBOW, true  ).max(110).min(-110)
                                        )
                                .hand(create("134", LEFT_HAND).min(-5).max(20)),
                        createArm(RIGHT_ARM)
                                .shoulder(RIGHT_SHOULDER,
                                        create("121", RIGHT_SHOULDER_ROLL, true),
                                        create("130", RIGHT_SHOULDER_PITCH, true)
                                        )
                                .elbow(
                                        create("133", RIGHT_ELBOW, true).max(110).min(-110)
                                )
                                .hand(create("124", RIGHT_HAND, true).min(-5).max(20)))
//                .head("head",
//                        create("140", PITCH_HEAD).min(-25).max(25),
//                        create("140", ROLL_HEAD).min(-50).max(50))
                .sensor(new Ina260CurrentSensor("test", "ina260"))
                .sensor(new LSM9DS1GyroSensor("test", "lsm9ds1"))
                .behaviourController(new CartesianControlImpl())
                .behaviourController(new NavigationControlImpl())
                .behaviourController(new CoordinatesMonitor())
                .build(robot);
        configuredRobotRegistery.register(maximus);

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
