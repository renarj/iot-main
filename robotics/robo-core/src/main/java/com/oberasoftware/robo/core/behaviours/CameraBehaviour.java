package com.oberasoftware.robo.core.behaviours;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.robo.core.behaviours.servos.impl.SingleServoBehaviour;
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
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
        tilt.initialize(behaviouralRobot, robotCore);
        rotate.initialize(behaviouralRobot, robotCore);
    }

    public void rotate(int speed, Scale scale) {
        LOG.info("Received position: {} for camera rotation", speed);
        goToPosition(rotate, speed, scale);
    }

    public void tilt(int speed, Scale scale) {
        LOG.info("Received position: {} for camera tilt", speed);
        goToPosition(tilt, speed, scale);
    }

    private void goToPosition(SingleServoBehaviour servo, int speed, Scale scale) {
        int percentage = (speed + 100) / 2;

        LOG.info("Going to percentage: {}", percentage);
        servo.goToPosition(10, scale, percentage);
    }
}
