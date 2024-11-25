package com.oberasoftware.robot.nao;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.iot.core.robotics.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class NaoServo {

    private final String servoId;
    private final ALMotion alMotion;

    public NaoServo(String servoId, ALMotion alMotion) {
        this.servoId = servoId;
        this.alMotion = alMotion;
    }

    public String getId() {
        return servoId;
    }

    public ServoData getData() {
        return null;
    }

    public void moveTo(int position) {

    }

    public void setSpeed(int speed) {

    }

    public void setTorgueLimit(int torgueLimit) {

    }

    public void enableTorgue() {

    }

    public void disableTorgue() {

    }
}
