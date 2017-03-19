package com.oberasoftware.max.core.behaviours.gripper;

import com.oberasoftware.max.core.behaviours.gripper.impl.GripperImpl;
import com.oberasoftware.max.core.behaviours.gripper.impl.LiftableRotatableGripperImpl;
import com.oberasoftware.max.core.behaviours.gripper.impl.RotatableGripperImpl;
import com.oberasoftware.max.core.behaviours.servos.ServoBehaviour;

/**
 * @author renarj
 */
public class GripperBuilder {
    private ServoBehaviour leftGripper;
    private ServoBehaviour rightGripper;

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
