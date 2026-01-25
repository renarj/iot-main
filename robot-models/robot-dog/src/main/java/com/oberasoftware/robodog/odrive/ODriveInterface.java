package com.oberasoftware.robodog.odrive;

public interface ODriveInterface {
    void open();

    void close();

    void discoverDrives();

    void setAxisState(int nodeId, ODriveState state);

    void setControllerModes(int nodeId, ControlMode controlMode, InputMode inputMode);

    void setInputPosition(int nodeId, float turns);

    void setInputVelocity(int nodeId, float vel);

    void setLimits(int nodeId, float velLimit, float current);

    void setTrajectorLimit(int nodeId, float velLimit);

    void rebootDrive(int nodeId);

    void requestCurrentEstimate(int nodeId);

    void requestEncoderEstimates(int nodeId);

    void readParameter(int nodeId, int endpointId);

    void writeParameter(int nodeId, int endpointId, float value);

    void requestBusVoltage(int nodeId);

    void requestTemperature(int nodeId);

    void registerListener(ODriveEventListener listener);

    void removeListener(ODriveEventListener listener);

    void setPositionGain(int nodeId, float gain);

    void setVelocityGain(int nodeId, float gain, float integratorGain);
}
