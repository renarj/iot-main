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
    private final int temperature;
    private final double voltage;

    public SimpleServo(String servoId, int speed, int position, int torgue, int temperature, double voltage) {
        this.servoId = servoId;
        this.speed = speed;
        this.position = position;
        this.torgue = torgue;
        this.temperature = temperature;
        this.voltage = voltage;
    }

    public SimpleServo(Servo servo) {
        this.servoId = servo.getId();
        this.speed = servo.getData().getValue(ServoProperty.SPEED);
        this.position = servo.getData().getValue(ServoProperty.POSITION);
        Integer t = servo.getData().getValue(ServoProperty.TEMPERATURE);
        Double c = servo.getData().getValue(ServoProperty.VOLTAGE);
        this.temperature = t != null ? t : 0;
        this.voltage = c != null ? c : 0.0;
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

    public int getTemperature() {
        return temperature;
    }

    public double getVoltage() {
        return voltage;
    }

    public int getPosition() {
        return position;
    }
}
