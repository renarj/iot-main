package com.oberasoftware.robomax.web;

import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoProperty;

/**
 * @author Renze de Vries
 */
public class SimpleServo {
    private final String servoId;
    private final int speed;
    private final int position;
    private final int torgue;

    public SimpleServo(String servoId, int speed, int position, int torgue) {
        this.servoId = servoId;
        this.speed = speed;
        this.position = position;
        this.torgue = torgue;
    }

    public SimpleServo(Servo servo) {
        this.servoId = servo.getId();
        this.speed = servo.getData().getValue(ServoProperty.SPEED);
        this.position = servo.getData().getValue(ServoProperty.POSITION);
        this.torgue = 0;
    }

    public String getServoId() {
        return servoId;
    }

    public int getTorgue() {
        return torgue;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPosition() {
        return position;
    }
}
