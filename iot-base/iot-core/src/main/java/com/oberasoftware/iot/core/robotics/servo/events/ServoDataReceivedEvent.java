package com.oberasoftware.iot.core.robotics.servo.events;

import com.oberasoftware.iot.core.robotics.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class ServoDataReceivedEvent extends ServoDataEvent {

    public ServoDataReceivedEvent(String servoId, ServoData updateData) {
        super(servoId, updateData);
    }
}
