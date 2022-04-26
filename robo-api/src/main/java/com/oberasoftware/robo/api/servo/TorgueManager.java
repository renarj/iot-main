package com.oberasoftware.robo.api.servo;

import com.oberasoftware.robo.api.Capability;

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
