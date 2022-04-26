package com.oberasoftware.robo.api.behavioural.wheel;


import com.oberasoftware.robo.api.behavioural.Behaviour;

/**
 * @author renarj
 */
public interface Wheel extends Behaviour {
    String getName();

    void forward(int speed);

    void backward(int speed);

    /**
     * Move the wheel in any direction, negative speed is backward, positive speed is forward drive
     * @param speed Positive input means forward, negative means backward
     */
    void move(int speed);

    void stop();
}
