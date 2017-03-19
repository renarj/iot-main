package com.oberasoftware.max.core.behaviours.wheels;

import com.oberasoftware.max.core.behaviours.Behaviour;

/**
 * @author renarj
 */
public interface Wheel extends Behaviour {
    void forward(int speed);

    void backward(int speed);

    void stop();
}
