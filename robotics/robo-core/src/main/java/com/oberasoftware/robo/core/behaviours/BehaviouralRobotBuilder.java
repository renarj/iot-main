package com.oberasoftware.robo.core.behaviours;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveBehaviour;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveTrain;
import com.oberasoftware.iot.core.robotics.navigation.RobotNavigationController;
import com.oberasoftware.robo.core.behaviours.gripper.GripperBuilder;
import com.oberasoftware.robo.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.robo.core.behaviours.wheels.impl.DriveBehaviourImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author renarj
 */
public class BehaviouralRobotBuilder {
    private RobotHardware robot;

    private List<Behaviour> behaviours;

    private BehaviouralRobotBuilder(RobotHardware robot) {
        this.robot = robot;
        this.behaviours = new ArrayList<>();
    }

    public static BehaviouralRobotBuilder create(RobotHardware robot) {
        return new BehaviouralRobotBuilder(robot);
    }

    public BehaviouralRobotBuilder gripper(GripperBuilder gripperBuilder) {
        behaviours.add(gripperBuilder.build());
        return this;
    }

    public BehaviouralRobotBuilder camera(SingleServoBehaviour tilt, SingleServoBehaviour rotate) {
        behaviours.add(new CameraBehaviour(tilt, rotate));
        return this;
    }

    public BehaviouralRobotBuilder wheels(DriveTrain left, DriveTrain right) {
        behaviours.add(new DriveBehaviourImpl(left, right));
        return this;
    }

    public BehaviouralRobotBuilder wheels(DriveBehaviour behaviour) {
        behaviours.add(behaviour);
        return this;
    }

    public BehaviouralRobotBuilder navigation(RobotNavigationController controller) {
        this.behaviours.add(controller);
        return this;
    }


    public Robot build(String controllerId) {
        RobotImpl behaviouralRobot = new RobotImpl(controllerId, robot, behaviours);
        behaviouralRobot.initialize();

        return behaviouralRobot;
    }
}
