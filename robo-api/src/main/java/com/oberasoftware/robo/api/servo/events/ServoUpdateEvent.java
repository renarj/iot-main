package com.oberasoftware.robo.api.servo.events;

import com.oberasoftware.robo.api.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class ServoUpdateEvent extends ServoDataEvent {

    public ServoUpdateEvent(String servoId, ServoData updateData) {
        super(servoId, updateData);
    }
}
