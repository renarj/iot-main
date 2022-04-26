package com.oberasoftware.robo.api.servo.events;

import com.oberasoftware.robo.api.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class ServoDataReceivedEvent extends ServoDataEvent {

    public ServoDataReceivedEvent(String servoId, ServoData updateData) {
        super(servoId, updateData);
    }
}
