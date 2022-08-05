package com.oberasoftware.max.core.behaviours.servos.impl;

import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.behavioural.ServoBehaviour;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
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
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robot) {
        this.servoDriver = robot.getServoDriver();
    }

    @Override
    public void goToPosition(int speed, Scale scale, int percentage) {
        int delta = ((maximumPosition - minimumPosition) / 100) * percentage;
        int position = delta + minimumPosition;

        LOG.info("Setting target position for servo: {} to percentage position: {}", servoId, position);
        servoDriver.setPositionAndSpeed(servoId, 10, new Scale(0, 100), position, new Scale(0, 2000));
    }

    @Override
    public void goToMinimum(int speed, Scale scale) {
        LOG.info("Setting target position for servo: {} to minimum: {}", servoId, minimumPosition);
        servoDriver.setPositionAndSpeed(servoId, speed, scale, minimumPosition, new Scale(0, 2000));
    }

    @Override
    public void goToMaximum(int speed, Scale scale) {
        LOG.info("Setting target position for servo: {} to maximum: {}", servoId, maximumPosition);
        servoDriver.setPositionAndSpeed(servoId, speed, scale, maximumPosition, new Scale(0, 2000));
    }

    @Override
    public void goToDefault(int speed, Scale scale) {
        LOG.info("Setting target position for servo: {} to default: {}", servoId, defaultPosition);
        servoDriver.setPositionAndSpeed(servoId, speed, scale, defaultPosition, new Scale(0, 2000));
    }
}
