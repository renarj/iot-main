package com.oberasoftware.max.core;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.max.core.behaviours.Behaviour;
import com.oberasoftware.max.core.behaviours.gripper.GripperBuilder;
import com.oberasoftware.max.core.behaviours.wheels.DriveTrain;
import com.oberasoftware.max.core.behaviours.wheels.impl.DriveBehaviourImpl;

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

    public BehaviouralRobotBuilder wheels(DriveTrain left, DriveTrain right) {
        behaviours.add(new DriveBehaviourImpl(left, right));
        return this;
    }

    public BehaviouralRobot build() {
        BehaviouralRobotImpl behaviouralRobot = new BehaviouralRobotImpl(robot, behaviours);
        behaviouralRobot.initialize();

        return behaviouralRobot;
    }
}
