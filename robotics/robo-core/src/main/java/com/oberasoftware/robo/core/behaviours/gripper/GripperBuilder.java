package com.oberasoftware.robo.core.behaviours.gripper;

import com.oberasoftware.iot.core.robotics.behavioural.ServoBehaviour;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.GripperBehaviour;
import com.oberasoftware.robo.core.behaviours.gripper.impl.GripperImpl;
import com.oberasoftware.robo.core.behaviours.gripper.impl.LiftableRotatableGripperImpl;
import com.oberasoftware.robo.core.behaviours.gripper.impl.RotatableGripperImpl;

/**
 * @author renarj
 */
public class GripperBuilder {
    private final ServoBehaviour leftGripper;
    private final ServoBehaviour rightGripper;

    private ServoBehaviour rotator;
    private ServoBehaviour elevator;

    public GripperBuilder(ServoBehaviour leftGripper, ServoBehaviour rightGripper) {
        this.leftGripper = leftGripper;
        this.rightGripper = rightGripper;
    }

    public static GripperBuilder create(ServoBehaviour leftGripper, ServoBehaviour rightGripper) {
        return new GripperBuilder(leftGripper, rightGripper);
    }

    public GripperBuilder rotation(ServoBehaviour rotationBehaviour) {
        this.rotator = rotationBehaviour;
        return this;
    }

    public GripperBuilder elevator(ServoBehaviour elevateServo) {
        this.elevator = elevateServo;
        return this;
    }

    public GripperBehaviour build() {
        if(rotator != null && elevator != null) {
            return new LiftableRotatableGripperImpl(leftGripper, rightGripper, rotator, elevator);
        } else if (rotator != null) {
            return new RotatableGripperImpl(leftGripper, rightGripper, rotator);
        } else {
            return new GripperImpl(leftGripper, rightGripper);
        }
    }
}
