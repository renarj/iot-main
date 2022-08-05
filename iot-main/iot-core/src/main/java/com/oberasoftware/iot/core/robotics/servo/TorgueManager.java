package com.oberasoftware.iot.core.robotics.servo;

import com.oberasoftware.iot.core.robotics.Capability;

import java.util.Map;

public interface TorgueManager extends Capability {
    enum ServoState {
        ON,
        OFF;

        public int toInt() {
            return this == ON ? 1 : 0;
        }
    }

    ServoState getState(String servoId);

    Map<String, ServoState> getStates();
}
