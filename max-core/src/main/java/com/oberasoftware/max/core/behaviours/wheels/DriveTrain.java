package com.oberasoftware.max.core.behaviours.wheels;

import com.oberasoftware.max.core.behaviours.Behaviour;

import java.util.List;

/**
 * @author renarj
 */
public interface DriveTrain extends Behaviour {
    void forward(int speed);

    void backward(int speed);

    void stop();

    List<Wheel> getWheels();
}
