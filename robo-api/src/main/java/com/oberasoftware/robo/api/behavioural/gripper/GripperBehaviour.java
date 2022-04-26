package com.oberasoftware.robo.api.behavioural.gripper;


import com.oberasoftware.robo.api.behavioural.Behaviour;

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
