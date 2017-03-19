package com.oberasoftware.robo.container;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.max.core.BehaviouralRobot;
import com.oberasoftware.max.core.BehaviouralRobotBuilder;
import com.oberasoftware.max.core.BehaviouralRobotRegistry;
import com.oberasoftware.max.core.behaviours.gripper.GripperBuilder;
import com.oberasoftware.max.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.DriveTrain;
import com.oberasoftware.max.core.behaviours.wheels.impl.SimpleDriveTrainImpl;
import com.oberasoftware.max.core.behaviours.wheels.impl.WheelAction;
import com.oberasoftware.max.core.behaviours.wheels.impl.WheelImpl;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.motion.JsonMotionResource;
import com.oberasoftware.robo.dynamixel.motion.RoboPlusMotionEngine;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.newArrayList;
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

    @Value("dynamixelPort:")
    private String dynamixelPort;

    public void initialize() {
        LOG.info("Connecting to Dynamixel servo port: {}", dynamixelPort);
        Robot robot = new SpringAwareRobotBuilder("max", applicationContext)
                .motionEngine(RoboPlusMotionEngine.class,
                        new JsonMotionResource("/basic-animations.json")
                )
                .servoDriver(DynamixelServoDriver.class,
                        ImmutableMap.<String, String>builder()
                                .put(DynamixelServoDriver.PORT, dynamixelPort).build())
                .build();
        robotRegistry.register(robot);
        LOG.info("Low level robot created: {}", robot);

        WheelAction forwardAction = (si, s, d) -> d.setServoSpeed(si, s);
        WheelAction backwardAction = (si, s, d) -> d.setServoSpeed(si, s + 1024);

        DriveTrain left = new SimpleDriveTrainImpl(newArrayList(
                new WheelImpl("5", forwardAction, backwardAction),
                new WheelImpl("11", true, forwardAction, backwardAction)));
        DriveTrain right = new SimpleDriveTrainImpl(newArrayList(
                new WheelImpl("2", forwardAction, backwardAction),
                new WheelImpl("16", true, forwardAction, backwardAction)));

        BehaviouralRobot robotCar = BehaviouralRobotBuilder.create(robot)
                .gripper(GripperBuilder.create(
                        new SingleServoBehaviour("3", 640, 420, 640),
                        new SingleServoBehaviour("4", 426, 570, 582))
                        .rotation(new SingleServoBehaviour("10", 350, 650, 512))
                        .elevator(new SingleServoBehaviour("14", 750, 600, 750)))
                .wheels(left, right)
                .build();
        behaviouralRobotRegistry.register(robotCar);
        LOG.info("Robot: {} was registered", robotCar);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }
}
