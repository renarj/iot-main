package com.oberasoftware.iot.core.robotics.behavioural.wheel;


import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;

/**
 * @author renarj
 */
public interface Wheel extends Behaviour {
    String getName();

    String getControllerId();

    String getThingId();

    String getServoId();
}
