package com.oberasoftware.robo.maximus.motion;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.Joint;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;
import com.oberasoftware.robo.api.behavioural.humanoid.MotionControl;
import com.oberasoftware.robo.api.commands.BulkPositionSpeedCommand;
import com.oberasoftware.robo.api.commands.PositionAndSpeedCommand;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.maximus.model.JointDataImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MotionControlImpl implements MotionControl {
    private static final Logger LOG = getLogger(MotionControlImpl.class);

    public static final Scale RADIAL_SCALE = new Scale(-180, 180);

    private ServoDriver servoDriver;
    private MotionEngine motionEngine;
    private MotionStorage motionStorage;

    private Map<String, Joint> jointMap;

    public MotionControlImpl(List<Joint> joints) {
        jointMap = joints.stream().collect(Collectors.toMap(Joint::getID, jv -> jv));
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        this.servoDriver = robotCore.getServoDriver();
        this.motionEngine = behaviouralRobot.getBehaviour(MotionEngine.class);
        this.motionStorage = robotCore.getCapability(MotionStorage.class);
    }

    @Override
    public JointData getJointData(String jointId) {
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
    public List<JointData> getJointsData() {
        return servoDriver.getServos()
                .stream()
                .map(s -> convert(s.getId(), s.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public Joint getJoint(String jointId) {
        return jointMap.get(jointId);
    }

    @Override
    public List<Joint> getJoints() {
        return new ArrayList<>(jointMap.values());
    }

    @Override
    public void setJointPosition(JointData position) {
        LOG.info("Setting joint position for ID: {} to: {}", position.getId(), position.getDegrees());

        if(jointMap.containsKey(position.getId())) {
            Joint joint = jointMap.get(position.getId());
            if(validateDegrees(position, joint)) {
                servoDriver.setTargetPosition(position.getId(), correctInversion(joint, position.getDegrees()), RADIAL_SCALE);
            } else {
                throw new RoboException(String.format("Joint: %s position: %s exceeds minimum: %d and Maximum: %d",
                        position.getId(), position.getDegrees(), joint.getMinDegrees(), joint.getMaxDegrees()));
            }
        }
    }

    @Override
    public void setJointPositions(List<JointData> jointPositions) {
        Map<String, PositionAndSpeedCommand> commands = jointPositions.stream()
                .filter(j -> jointMap.containsKey(j.getId()))
                .filter(j -> validateDegrees(j, jointMap.get(j.getId())))
                .map(j -> new PositionAndSpeedCommand(j.getId(), correctInversion(jointMap.get(j.getId()), j.getDegrees()), RADIAL_SCALE, 0, new Scale(0, 100)))
                .collect(Collectors.toMap(PositionAndSpeedCommand::getServoId, c -> c));

        LOG.info("Setting bulk positions: {}", commands);

        servoDriver.bulkSetPositionAndSpeed(commands, BulkPositionSpeedCommand.WRITE_MODE.REGISTERED_WRITE);
    }

    @Override
    public void runMotion(String motionId) {
        Motion motion = motionStorage.findMotion(motionId);
        if(motion != null) {
            LOG.info("Submitting motion: {} for execution", motion);
            motionEngine.post(motion);
        } else {
            throw new RoboException("Could not find motion: " + motionId);
        }
    }

    private int correctInversion(Joint joint, int degrees) {
        if(joint.isInverted()) {
            return -degrees;
        } else {
            return degrees;
        }
    }

    private boolean validateDegrees(JointData desiredPosition, Joint actual) {
        return desiredPosition.getDegrees() > actual.getMinDegrees() && desiredPosition.getDegrees() < actual.getMaxDegrees();
    }

    private ServoData getServoData(String servoId) {
        Servo servo = servoDriver.getServo(servoId);
        return servo.getData();
    }
}
