package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.api.servo.ServoProperty;

import java.util.List;
import java.util.stream.Collectors;

public class MotionControlImpl implements MotionControl {

    private static final Scale RADIAL_SCALE = new Scale(-180, 180);

    private ServoDriver servoDriver;

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        this.servoDriver = robotCore.getServoDriver();
    }

    @Override
    public JointData getJoint(String jointId) {
        ServoData data = getServoData(jointId);

        return convert(jointId, data);
    }

    private JointData convert(String jointId, ServoData data) {
        int position = data.getValue(ServoProperty.POSITION);
        Scale scale = data.getValue(ServoProperty.POSITION_SCALE);
        int converted = scale.convertToScale(position, RADIAL_SCALE);

        return new JointDataImpl(jointId, converted, position);
    }

    @Override
    public List<JointData> getJoints() {
        return servoDriver.getServos()
                .stream()
                .map(s -> convert(s.getId(), s.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public void setJointPosition(JointData position) {

    }

    @Override
    public void setJointPositions(List<JointData> jointPositions) {

    }

    private ServoData getServoData(String servoId) {
        Servo servo = servoDriver.getServo(servoId);
        return servo.getData();


    }
}
