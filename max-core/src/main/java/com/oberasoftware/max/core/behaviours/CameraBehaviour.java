package com.oberasoftware.max.core.behaviours;

import com.oberasoftware.max.core.behaviours.servos.impl.SingleServoBehaviour;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.commands.Scale;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class CameraBehaviour implements Behaviour {
    private static final Logger LOG = getLogger(CameraBehaviour.class);

    private final SingleServoBehaviour tilt;
    private final SingleServoBehaviour rotate;

    public CameraBehaviour(SingleServoBehaviour tilt, SingleServoBehaviour rotate) {
        this.tilt = tilt;
        this.rotate = rotate;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        tilt.initialize(behaviouralRobot, robotCore);
        rotate.initialize(behaviouralRobot, robotCore);
    }

    public void rotate(int speed, Scale scale) {
        LOG.info("Received speed: {} for camera rotation", speed);
        handleSpeed(rotate, speed, scale);
    }

    public void tilt(int speed, Scale scale) {
        LOG.info("Received speed: {} for camera tilt", speed);
        handleSpeed(tilt, speed, scale);
    }

    private void handleSpeed(SingleServoBehaviour servo, int speed, Scale scale) {
        int pSpeed = Math.abs(speed);
        if(speed < 0) {
            LOG.info("Going to minimum at speed: {}", pSpeed);
            servo.goToMinimum(pSpeed, scale);
        } else if(speed > 0) {
            LOG.info("Going to maximum at speed: {}", pSpeed);
            servo.goToMaximum(pSpeed, scale);
        } else {
            LOG.info("Going to default at speed: {}", pSpeed);
            servo.goToDefault(pSpeed, scale);
        }

    }
}
