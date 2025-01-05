package com.oberasoftware.robo.core.behaviours.wheels.impl;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.servo.StateManager;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class WheelImpl implements Wheel {
    private static final Logger LOG = getLogger(WheelImpl.class);

    private final String controllerId;
    private final String thingId;
    private final String servoId;
    private final boolean reversed;

    public WheelImpl(String controllerId, String thingId, String servoId, boolean reversed) {
        this.controllerId = controllerId;
        this.thingId = thingId;
        this.servoId = servoId;
        this.reversed = reversed;
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
        var stateManager = robotCore.getCapability(StateManager.class);
        stateManager.setServoMode(servoId, StateManager.ServoMode.VELOCITY_CONTROL);
    }

    @Override
    public String getName() {
        return servoId;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    @Override
    public String getServoId() {
        return servoId;
    }
}
