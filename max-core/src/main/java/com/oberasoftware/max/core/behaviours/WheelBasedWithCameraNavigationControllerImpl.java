package com.oberasoftware.max.core.behaviours;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.DriveBehaviour;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.navigation.DirectionalInput;
import com.oberasoftware.robo.api.navigation.RobotNavigationController;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class WheelBasedWithCameraNavigationControllerImpl implements RobotNavigationController {
    private static final Logger LOG = getLogger(WheelBasedWithCameraNavigationControllerImpl.class);

    private BehaviouralRobot behaviouralRobot;


    @Override
    public void move(DirectionalInput input) {
        LOG.info("Received direction input: {}", input);

        if(input.hasInputAxis("tilt") || input.hasInputAxis("rotational")) {
            handleCameraControl(input);
        } else if (input.hasInputAxis("x") || input.hasInputAxis("y") || input.hasInputAxis("z")) {
            moveBody(input);
        }
    }

    private void handleCameraControl(DirectionalInput input) {
        Double rotateCamera = input.getAxis("rotational");
        Double tiltCamera = input.getAxis("tilt");


    }

    private void moveBody(DirectionalInput input) {
        DriveBehaviour driveBehaviour = behaviouralRobot.getBehaviour(DriveBehaviour.class);
        driveBehaviour.drive(input, new Scale(-100, 100));
    }

    @Override
    public DirectionalInput getNavigationDirections() {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robot) {
        this.behaviouralRobot = behaviouralRobot;
    }
}
