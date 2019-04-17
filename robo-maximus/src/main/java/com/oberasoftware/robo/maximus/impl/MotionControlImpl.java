package com.oberasoftware.robo.maximus.impl;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.api.servo.ServoProperty;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MotionControlImpl implements MotionControl {
    private static final Logger LOG = getLogger(MotionControlImpl.class);

    public static final Scale RADIAL_SCALE = new Scale(-180, 180);

    private ServoDriver servoDriver;

    private Map<String, Joint> jointMap;

    MotionControlImpl(List<Joint> joints) {
        jointMap = joints.stream().collect(Collectors.toMap(Joint::getID, jv -> jv));
    }

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
        LOG.info("Setting joint position for ID: {} to: {}", position.getId(), position.getDegrees());

        if(jointMap.containsKey(position.getId())) {
            Joint joint = jointMap.get(position.getId());
            if(position.getDegrees() > joint.getMinDegrees() && position.getDegrees() < joint.getMaxDegrees()) {
                servoDriver.setTargetPosition(position.getId(), position.getDegrees(), RADIAL_SCALE);
            } else {
                throw new RoboException(String.format("Joint: %s position: %s exceeds minimum: %d and Maximum: %d",
                        position.getId(), position.getDegrees(), joint.getMinDegrees(), joint.getMaxDegrees()));
            }
        }
    }

    @Override
    public void setJointPositions(List<JointData> jointPositions) {
        jointPositions.forEach(this::setJointPosition);
    }

    private ServoData getServoData(String servoId) {
        Servo servo = servoDriver.getServo(servoId);
        return servo.getData();


    }
}
