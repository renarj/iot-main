package com.oberasoftware.max.core.behaviours.servos.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.max.core.behaviours.servos.ServoBehaviour;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class SingleServoBehaviour implements ServoBehaviour {
    private static final Logger LOG = getLogger(SingleServoBehaviour.class);

    private ServoDriver servoDriver;
    private final String servoId;
    private final int minimumPosition;
    private final int maximumPosition;
    private final int defaultPosition;

    public SingleServoBehaviour(String servoId, int minimumPosition, int maximumPosition, int defaultPosition) {
        this.servoId = servoId;
        this.minimumPosition = minimumPosition;
        this.maximumPosition = maximumPosition;
        this.defaultPosition = defaultPosition;
    }

    @Override
    public void initialize(Robot robot) {
        this.servoDriver = robot.getServoDriver();
    }

    @Override
    public void goToPosition(int percentage) {
        int delta = ((maximumPosition - minimumPosition) / 100) * percentage;
        int position = delta + minimumPosition;

        LOG.info("Setting target position for servo: {} to percentage position: {}", servoId, position);
        servoDriver.setTargetPosition(servoId, position);
    }

    @Override
    public void goToMinimum() {
        LOG.info("Setting target position for servo: {} to minimum: {}", servoId, minimumPosition);
        servoDriver.setTargetPosition(servoId, minimumPosition);
    }

    @Override
    public void goToMaximum() {
        LOG.info("Setting target position for servo: {} to maximum: {}", servoId, maximumPosition);
        servoDriver.setTargetPosition(servoId, maximumPosition);
    }

    @Override
    public void goToDefault() {
        LOG.info("Setting target position for servo: {} to default: {}", servoId, defaultPosition);
        servoDriver.setTargetPosition(servoId, defaultPosition);
    }
}
