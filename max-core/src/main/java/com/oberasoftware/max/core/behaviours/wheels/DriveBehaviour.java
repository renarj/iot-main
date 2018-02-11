package com.oberasoftware.max.core.behaviours.wheels;

import com.oberasoftware.max.core.behaviours.Behaviour;

import java.util.Arrays;
import java.util.List;

/**
 * @author renarj
 */
public interface DriveBehaviour extends Behaviour {
    enum DIRECTION {
        FORWARD,
        FORWARD_LEFT,
        FORWARD_RIGHT,
        BACKWARD,
        BACKWARD_LEFT,
        BACKWARD_RIGHT,
        LEFT,
        RIGHT,
        STOP;

        public static DIRECTION fromString(String direction) {
            return Arrays.stream(values())
                    .filter(d -> d.name().equalsIgnoreCase(direction))
                    .findFirst().orElse(STOP);
        }
    }

    void drive(int speed, DIRECTION direction);

    void forward(int speed);

    void left(int speed);

    void backward(int speed);

    void right(int speed);

    void stop();

    List<Wheel> getWheels();
}
