package com.oberasoftware.iot.core.robotics.servo.events;

import com.oberasoftware.iot.core.robotics.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class ServoUpdateEvent extends ServoDataEvent {

    public ServoUpdateEvent(String servoId, ServoData updateData) {
        super(servoId, updateData);
    }
}
