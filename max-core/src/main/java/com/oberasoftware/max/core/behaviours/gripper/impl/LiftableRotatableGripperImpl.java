package com.oberasoftware.max.core.behaviours.gripper.impl;


import com.oberasoftware.robo.api.behavioural.LiftableRotatableGripper;
import com.oberasoftware.robo.api.behavioural.ServoBehaviour;

/**
 * @author renarj
 */
public class LiftableRotatableGripperImpl extends RotatableGripperImpl implements LiftableRotatableGripper {

    private final ServoBehaviour lifter;

    public LiftableRotatableGripperImpl(ServoBehaviour leftArm, ServoBehaviour rightArm, ServoBehaviour rotator, ServoBehaviour lifter) {
        super(leftArm, rightArm, rotator);
        this.lifter = lifter;
    }

    @Override
    public void lift(int percentage) {
        lifter.goToPosition(10, ServoBehaviour.DEFAULT_SPEED_SCALE, percentage);
    }
}
