package com.oberasoftware.iot.core.robotics.behavioural.wheel;

import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.GripperBehaviour;

import java.util.Optional;

public interface DriveableRobot extends Robot {
    <T extends GripperBehaviour> Optional<T> getGripper();

    Optional<DriveBehaviour> getWheels();
}
