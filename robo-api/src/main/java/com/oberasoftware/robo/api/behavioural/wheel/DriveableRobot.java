package com.oberasoftware.robo.api.behavioural.wheel;

import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.gripper.GripperBehaviour;

import java.util.Optional;

public interface DriveableRobot extends BehaviouralRobot {
    <T extends GripperBehaviour> Optional<T> getGripper();

    Optional<DriveBehaviour> getWheels();
}
