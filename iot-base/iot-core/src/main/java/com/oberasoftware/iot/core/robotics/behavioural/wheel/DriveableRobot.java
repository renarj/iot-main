package com.oberasoftware.iot.core.robotics.behavioural.wheel;

import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.GripperBehaviour;

import java.util.Optional;

public interface DriveableRobot extends BehaviouralRobot {
    <T extends GripperBehaviour> Optional<T> getGripper();

    Optional<DriveBehaviour> getWheels();
}
