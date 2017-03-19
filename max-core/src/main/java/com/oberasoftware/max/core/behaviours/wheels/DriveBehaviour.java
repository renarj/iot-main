package com.oberasoftware.max.core.behaviours.wheels;

import com.oberasoftware.max.core.behaviours.Behaviour;

/**
 * @author renarj
 */
public interface DriveBehaviour extends Behaviour {
    void forward(int speed);

    void left(int speed);

    void backward(int speed);

    void right(int speed);

    void stop();
}
