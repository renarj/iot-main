package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.maximus.ConfigurableRobotBuilder;

public class RobotContext {
    private final HardwareRobotBuilder hardwareBuilder;
    private final ConfigurableRobotBuilder robotBuilder;

    public RobotContext(HardwareRobotBuilder hardwareBuilder, ConfigurableRobotBuilder robotBuilder) {
        this.hardwareBuilder = hardwareBuilder;
        this.robotBuilder = robotBuilder;
    }

    public HardwareRobotBuilder getHardwareBuilder() {
        return hardwareBuilder;
    }

    public ConfigurableRobotBuilder getRobotBuilder() {
        return robotBuilder;
    }
}
