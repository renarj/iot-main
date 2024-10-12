package com.oberasoftware.robo.core.motion;

import com.oberasoftware.iot.core.robotics.motion.JointTarget;

/**
 * @author Renze de Vries
 */
public class JointTargetImpl implements JointTarget {

    private String jointId;
    private int targetPosition;
    private int targetAngle = 0;
    private int targetVelocity = 0;

    public JointTargetImpl(String jointId, int targetPosition, int targetAngle) {
        this.jointId = jointId;
        this.targetPosition = targetPosition;
        this.targetAngle = targetAngle;
    }

    public JointTargetImpl(String jointId, int targetPosition) {
        this.jointId = jointId;
        this.targetPosition = targetPosition;
    }

    public JointTargetImpl() {
    }

    @Override
    public int getTargetAngle() {
        return targetAngle;
    }

    public void setTargetAngle(int targetAngle) {
        this.targetAngle = targetAngle;
    }

    @Override
    public String getJointId() {
        return jointId;
    }

    public void setJointId(String jointId) {
        this.jointId = jointId;
    }

    @Override
    public int getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }

    @Override
    public int getTargetVelocity() {
        return targetVelocity;
    }

    public void setTargetVelocity(int targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    @Override
    public String toString() {
        return "JointTargetImpl{" +
                "jointId='" + jointId + '\'' +
                ", targetPosition=" + targetPosition +
                ", targetAngle=" + targetAngle +
                ", targetVelocity=" + targetVelocity +
                '}';
    }
}
