package com.oberasoftware.max.core;

import com.oberasoftware.max.core.behaviours.CameraBehaviour;
import com.oberasoftware.max.core.behaviours.gripper.GripperBuilder;
import com.oberasoftware.max.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.impl.DriveBehaviourImpl;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.DriveBehaviour;
import com.oberasoftware.robo.api.behavioural.DriveTrain;
import com.oberasoftware.robo.api.navigation.RobotNavigationController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author renarj
 */
public class BehaviouralRobotBuilder {
    private Robot robot;

    private List<Behaviour> behaviours;

    private BehaviouralRobotBuilder(Robot robot) {
        this.robot = robot;
        this.behaviours = new ArrayList<>();
    }

    public static BehaviouralRobotBuilder create(Robot robot) {
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


    public BehaviouralRobot build() {
        BehaviouralRobotImpl behaviouralRobot = new BehaviouralRobotImpl(robot, behaviours);
        behaviouralRobot.initialize();

        return behaviouralRobot;
    }
}
