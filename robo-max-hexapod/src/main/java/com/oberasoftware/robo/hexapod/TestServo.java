package com.oberasoftware.robo.hexapod;

public class TestServo {
    private final String servoId;

    public TestServo(String servoId) {
        this.servoId = servoId;
    }

    public String getServoId() {
        return servoId;
    }

    @Override
    public String toString() {
        return "Servo{" +
                "servoId='" + servoId + '\'' +
                '}';
    }
}
