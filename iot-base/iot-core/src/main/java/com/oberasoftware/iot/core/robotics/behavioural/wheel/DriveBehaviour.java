package com.oberasoftware.iot.core.robotics.behavioural.wheel;

import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.navigation.DirectionalInput;

import java.util.List;

/**
 * @author renarj
 */
public interface DriveBehaviour extends Behaviour {
    void drive(DirectionalInput input, Scale scale);

    void forward(int speed, Scale scale);

    void left(int speed, Scale scale);

    void backward(int speed, Scale scale);

    void right(int speed, Scale scale);

    void stop();
}
