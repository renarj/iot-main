package com.oberasoftware.iot.core.robotics.servo;

import com.oberasoftware.iot.core.robotics.Capability;

import java.util.List;
import java.util.Map;

public interface StateManager extends Capability {
    enum TorgueState {
        ON,
        OFF;

        public int toInt() {
            return this == ON ? 1 : 0;
        }
    }

    enum ServoMode {
        CURRENT_CONTROL,
        VELOCITY_CONTROL,
        POSITION_CONTROL

    }

    void setTorgue(String servoId, boolean b);

    void setTorgueAll(boolean state);

    void setTorgueAll(boolean state, List<String> servos);

    void setServoMode(String servoId, ServoMode mode);

    TorgueState getTorgue(String servoId);

    Map<String, TorgueState> getTorgues();

    ServoMode getServoMode(String servoId);

    Map<String, ServoMode> getServoModes();


}
