package com.oberasoftware.robodog.odrive.device;

import com.oberasoftware.robodog.odrive.ControlMode;
import com.oberasoftware.robodog.odrive.InputMode;
import com.oberasoftware.robodog.odrive.ODriveInterface;
import com.oberasoftware.robodog.odrive.ODriveState;

public class ODriveDevice {
    private final int nodeId;
    private final String name;
    private final ODriveInterface can;
    private final ODriveStateManager stateManager;

    public ODriveDevice(int nodeId, String name, ODriveInterface can, ODriveStateManager stateManager) {
        this.nodeId = nodeId;
        this.name = name;
        this.can = can;
        this.stateManager = stateManager;
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public void setState(ODriveState state) {
        can.setAxisState(nodeId, state);
    }

    public void setControllerModes(ControlMode controlMode, InputMode inputMode) {
        can.setControllerModes(nodeId, controlMode, inputMode);
    }

    public void moveToPosition(float position) {
        can.setInputPosition(nodeId, position);
    }

    public void setVelocity(float turnsPerSecond) {
        can.setInputVelocity(nodeId, turnsPerSecond);
    }

    public void stop() {
        can.setInputVelocity(nodeId, 0);
    }

    public void setLimits(float velLimit, float current) {
        can.setLimits(nodeId, velLimit, current);
    }

    public void setTrajectorLimit(float velLimit) {
        can.setTrajectorLimit(nodeId, velLimit);
    }

    public double readPosition() {
        return stateManager.getAttribute(nodeId, ODriveAttribute.POSITION);
    }

    public double readCurrentMeasured() {
        return stateManager.getAttribute(nodeId, ODriveAttribute.CURRENT);
    }

    public double readBusVoltage() {
        return stateManager.getAttribute(nodeId, ODriveAttribute.BUS_VOLTAGE);
    }

    public double readBusCurrent() {
        return stateManager.getAttribute(nodeId, ODriveAttribute.BUS_CURRENT);
    }

    public double readBoardTemperature() {
        return stateManager.getAttribute(nodeId, ODriveAttribute.BOARD_TEMPERATURE);
    }

    public void setPositionGain(float gain) {
        can.setPositionGain(nodeId, gain);
    }

    public void setVelocityGain(float gain, float integratorGain) {
        can.setVelocityGain(nodeId, gain, integratorGain);
    }
}
