package com.oberasoftware.max.core.behaviours.wheels.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.max.core.behaviours.wheels.DriveTrain;
import com.oberasoftware.max.core.behaviours.wheels.Wheel;

import java.util.List;

/**
 * @author renarj
 */
public class SimpleDriveTrainImpl implements DriveTrain {
    private List<Wheel> wheels;

    public SimpleDriveTrainImpl(List<Wheel> wheels) {
        this.wheels = wheels;
    }

    @Override
    public void initialize(Robot robot) {
        wheels.forEach(w -> w.initialize(robot));
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
        wheels.forEach(w -> w.stop());
    }
}
