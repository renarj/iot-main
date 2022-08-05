package com.oberasoftware.max.core.behaviours;

import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.DriveBehaviour;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.navigation.DirectionalInput;
import com.oberasoftware.iot.core.robotics.navigation.RobotNavigationController;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class WheelBasedWithCameraNavigationControllerImpl implements RobotNavigationController {
    private static final Logger LOG = getLogger(WheelBasedWithCameraNavigationControllerImpl.class);

    private BehaviouralRobot behaviouralRobot;

    private final static Scale DEFAULT_SCALE = new Scale(-100, 100);

    @Override
    public void move(DirectionalInput input) {
        LOG.info("Received direction input: {}", input);

        boolean cameraMode = input.hasInputAxis("cameraMode") && input.getAxis("cameraMode") > 0;

        if(cameraMode) {
            handleCameraControl(input);
        }

        if (input.hasInputAxis("x") || input.hasInputAxis("y")) {
            DirectionalInput correctedInput = input;
            if(cameraMode) {
                Map<String, Double> m = new HashMap<>();
                input.getInputAxis().forEach(a -> {
                    if(!a.equalsIgnoreCase("rotate")) {
                        m.put(a, input.getAxis(a));
                    }
                });

                correctedInput = new DirectionalInput(m);
            }

            moveBody(correctedInput);
        }
    }

    private void handleCameraControl(DirectionalInput input) {
        Double cameraMode = input.getAxis("cameraMode");
        LOG.info("Camera control mode set to: {}", cameraMode);
        if(cameraMode > 0) {
            CameraBehaviour behaviour = behaviouralRobot.getBehaviour(CameraBehaviour.class);
            if(input.hasInputAxis("rotate")) {
                Double rotateCamera = input.getAxis("rotate");
                behaviour.rotate(rotateCamera.intValue(), new Scale(-500, 500));
            }
            if(input.hasInputAxis("tilt")) {
                Double tiltCamera = input.getAxis("tilt");
                behaviour.tilt(tiltCamera.intValue(), new Scale(-500, 500));
            }
        }
    }

    private void moveBody(DirectionalInput input) {
        DriveBehaviour driveBehaviour = behaviouralRobot.getBehaviour(DriveBehaviour.class);
        driveBehaviour.drive(input, DEFAULT_SCALE);
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
