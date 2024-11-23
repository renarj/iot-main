package com.oberasoftware.home.rules.api.general;

import com.oberasoftware.home.rules.api.Statement;

public class ActivateMotion implements Statement {
    private String controllerId;
    private String motionId;

    public ActivateMotion(String controllerId, String motionId) {
        this.controllerId = controllerId;
        this.motionId = motionId;
    }

    public ActivateMotion() {
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getMotionId() {
        return motionId;
    }

    public void setMotionId(String motionId) {
        this.motionId = motionId;
    }

    @Override
    public String toString() {
        return "ActivateMotion{" +
                "controllerId='" + controllerId + '\'' +
                ", motionId='" + motionId + '\'' +
                '}';
    }
}
