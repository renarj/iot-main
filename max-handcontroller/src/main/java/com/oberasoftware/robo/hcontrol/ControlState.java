package com.oberasoftware.robo.hcontrol;

import com.oberasoftware.robo.api.navigation.DirectionalInput;

import java.util.HashMap;
import java.util.Map;

public class ControlState {

    private Map<String, Double> state = new HashMap<>();

    private boolean cameraControlMode = false;


    public ControlState() {

    }

    public void setState(String axis, Integer state) {
        this.state.put(axis, state.doubleValue());
    }

    public boolean isCameraControlMode() {
        return cameraControlMode;
    }

    public void setCameraControlMode(boolean cameraControlMode) {
        this.cameraControlMode = cameraControlMode;
    }

    public DirectionalInput getInput() {
        return new DirectionalInput(state);
    }

    @Override
    public String toString() {
        return "ControlState{" +
                "state=" + state +
                ", cameraControlMode=" + cameraControlMode +
                '}';
    }
}
