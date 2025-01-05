package com.oberasoftware.robo.maximus.motion;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.MotionEngine;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointData;
import com.oberasoftware.iot.core.robotics.motion.JointTarget;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.iot.core.robotics.servo.*;
import com.oberasoftware.robo.maximus.model.JointDataImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class JointControlImpl implements JointControl {
    private static final Logger LOG = getLogger(JointControlImpl.class);

    public static final Scale RADIAL_SCALE = new Scale(-180, 180);
    public static final String DEGREES = "degrees";

    private ServoDriver servoDriver;
    private MotionEngine motionEngine;
    private MotionStorage motionStorage;
    private Robot robot;

    private final Map<String, Joint> jointMap;

    private StateManager stateManager;

    public JointControlImpl(List<Joint> joints) {
        jointMap = joints.stream().collect(Collectors.toMap(Joint::getJointId, jv -> jv));
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
        this.robot = behaviouralRobot;
        this.servoDriver = robotCore.getServoDriver();
        this.motionEngine = behaviouralRobot.getBehaviour(MotionEngine.class);
        this.motionStorage = robotCore.getCapability(MotionStorage.class);
        this.stateManager = robotCore.getCapability(StateManager.class);
    }

    @Override
    public Optional<JointData> getJointData(String jointId) {
        if(jointMap.containsKey(jointId)) {
            Optional<ServoData> data = getServoData(jointId);
            if(data.isPresent()) {
                return Optional.of(convert(jointId, data.get()));
            }
        }
        return Optional.empty();
    }

    private JointData convert(String jointId, ServoData data) {
        Integer position = data.getValue(ServoProperty.POSITION);
        Scale scale = data.getValue(ServoProperty.POSITION_SCALE);
        Integer converted = scale.convertToScale(position, RADIAL_SCALE);

        Integer tState = 0;
        if(stateManager != null) {
            tState = stateManager.getTorgue(jointId).toInt();
        } else {
            LOG.debug("No torgue manager installed cannot track torgue state");
        }

        if(jointMap.get(jointId).isInverted()) {
            converted = -converted;
        }

        Map<String, Value> map = new ImmutableMap.Builder<String, Value>()
                .put(DEGREES, new ValueImpl(VALUE_TYPE.NUMBER, converted))
                .put(ServoProperty.POSITION.name().toLowerCase(), new ValueImpl(VALUE_TYPE.NUMBER, position))
                .put(ServoProperty.TORGUE.name().toLowerCase(), new ValueImpl(VALUE_TYPE.BOOLEAN, tState))
                .build();

        return new JointDataImpl(robot.getControllerId(), jointId, map);
    }

    @Override
    public List<JointData> getJointsData() {
        return servoDriver.getServos()
                .stream()
                .filter(s -> jointMap.containsKey(s.getId()))
                .map(s -> convert(s.getId(), s.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public Joint getJoint(String jointId) {
        return jointMap.get(jointId);
    }

    @Override
    public boolean isJointPresent(String jointId) {
        return jointMap.containsKey(jointId);
    }

    @Override
    public boolean isServoJointPresent(String servoId) {
        return jointMap.values().stream().anyMatch(j -> j.getServoId().equalsIgnoreCase(servoId));
    }

    @Override
    public Joint getServoJoint(String servoId) {
        return jointMap.values().stream().filter(j -> j.getServoId().equalsIgnoreCase(servoId)).findFirst()
                .orElseThrow(() -> new RuntimeIOTException("Could not find Joint for servo: " + servoId));
    }

    @Override
    public List<Joint> getJoints() {
        return new ArrayList<>(jointMap.values());
    }

    @Override
    public void setJointPosition(JointTarget position) {
        LOG.info("Setting joint position for ID: {} to: {}", position.getJointId(), position.getTargetAngle());

        if(jointMap.containsKey(position.getJointId())) {
            Joint joint = jointMap.get(position.getJointId());
            if(validateDegrees(position, joint)) {
                servoDriver.setPositionAndSpeed(position.getJointId(), 15, new Scale(0,0), correctInversion(joint, position.getTargetAngle()), RADIAL_SCALE);

//                servoDriver.setTargetPosition(position.getServoId(), correctInversion(joint, position.getTargetAngle()), RADIAL_SCALE);
            } else {
                throw new RoboException(String.format("Joint: %s position: %s exceeds minimum: %d and Maximum: %d",
                        position.getJointId(), position.getTargetAngle(), joint.getMinDegrees(), joint.getMaxDegrees()));
            }
        }
    }

    @Override
    public void setJointPositions(List<JointTarget> jointPositions) {
        Map<String, PositionAndSpeedCommand> commands = jointPositions.stream()
                .filter(j -> jointMap.containsKey(j.getJointId()))
                .filter(j -> validateDegrees(j, jointMap.get(j.getJointId())))
                .map(j -> new PositionAndSpeedCommand(j.getJointId(),
                        correctInversion(jointMap.get(j.getJointId()), j.getTargetAngle()), RADIAL_SCALE, 0, new Scale(0, 100)))
                .collect(Collectors.toMap(PositionAndSpeedCommand::getServoId, c -> c));

        LOG.info("Setting bulk positions: {}", commands);

        servoDriver.bulkSetPositionAndSpeed(commands, BulkPositionSpeedCommand.WRITE_MODE.REGISTERED_WRITE);
    }

    @Override
    public void runMotion(String controllerId, String motionId) {
        Motion motion = motionStorage.findMotion(controllerId, motionId);
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

    private boolean validateDegrees(JointTarget desiredPosition, Joint actual) {
        return desiredPosition.getTargetAngle() >= actual.getMinDegrees() && desiredPosition.getTargetAngle() <= actual.getMaxDegrees();
    }

    private Optional<ServoData> getServoData(String servoId) {
        Servo servo = servoDriver.getServo(servoId);
        return servo != null ? Optional.of(servo.getData()) : Optional.empty();
    }
}
