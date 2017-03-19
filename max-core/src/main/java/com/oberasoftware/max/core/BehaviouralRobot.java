package com.oberasoftware.max.core;

import com.oberasoftware.max.core.behaviours.Behaviour;
import com.oberasoftware.max.core.behaviours.gripper.GripperBehaviour;
import com.oberasoftware.max.core.behaviours.wheels.DriveBehaviour;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface BehaviouralRobot {
    String getRobotId();

    List<Behaviour> getBehaviours();

    <T extends Behaviour> T getBehaviour(Class<T> behaviourClass);

    <T extends GripperBehaviour> Optional<T> getGripper();

    Optional<DriveBehaviour> getWheels();
}
