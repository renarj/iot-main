package com.oberasoftware.iot.core.robotics.servo;

import com.oberasoftware.base.event.Event;

/**
 * @author Renze de Vries
 */
public interface ServoCommand extends Event {
    String getServoId();
}
