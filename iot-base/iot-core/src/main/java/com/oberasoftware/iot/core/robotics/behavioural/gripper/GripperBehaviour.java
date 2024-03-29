package com.oberasoftware.iot.core.robotics.behavioural.gripper;


import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;

/**
 * @author renarj
 */
public interface GripperBehaviour extends Behaviour {
    void open();

    void open(int percentage);

    void rest();

    void close();

    void close(int percentage);
}
