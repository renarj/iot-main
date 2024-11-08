package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.maximus.JointBasedRobotBuilder;

public class RobotContext {
    private final HardwareRobotBuilder hardwareBuilder;
    private final JointBasedRobotBuilder robotBuilder;

    public RobotContext(HardwareRobotBuilder hardwareBuilder, JointBasedRobotBuilder robotBuilder) {
        this.hardwareBuilder = hardwareBuilder;
        this.robotBuilder = robotBuilder;
    }

    public HardwareRobotBuilder getHardwareBuilder() {
        return hardwareBuilder;
    }

    public JointBasedRobotBuilder getRobotBuilder() {
        return robotBuilder;
    }
}
