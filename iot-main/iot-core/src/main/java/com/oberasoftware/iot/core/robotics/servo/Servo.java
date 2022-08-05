package com.oberasoftware.iot.core.robotics.servo;

import com.oberasoftware.iot.core.robotics.commands.Scale;

/**
 * @author Renze de Vries
 */
public interface Servo extends DynamixelDevice {

    ServoData getData();

    void moveTo(int position, Scale scale);

    void setSpeed(int speed, Scale scale);

    void setTorgueLimit(int torgueLimit);

    void enableTorgue();

    void disableTorgue();
}
