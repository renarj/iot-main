package com.oberasoftware.robo.core.behaviours.wheels.impl;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveTrain;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;

import java.util.List;

/**
 * @author renarj
 */
public class SimpleDriveTrainImpl implements DriveTrain {
    private final List<Wheel> wheels;

    public SimpleDriveTrainImpl(List<Wheel> wheels) {
        this.wheels = wheels;
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robot) {
        wheels.forEach(w -> w.initialize(behaviouralRobot, robot));
    }

    @Override
    public void forward(int speed) {
        wheels.forEach(w -> w.forward(speed));
    }

    @Override
    public void backward(int speed) {
        wheels.forEach(w -> w.backward(speed));
    }

    @Override
    public void stop() {
        wheels.forEach(Wheel::stop);
    }

    @Override
    public List<Wheel> getWheels() {
        return wheels;
    }
}
