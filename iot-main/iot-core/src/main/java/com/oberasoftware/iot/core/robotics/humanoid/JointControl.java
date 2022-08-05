package com.oberasoftware.iot.core.robotics.humanoid;

import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointData;
import com.oberasoftware.iot.core.robotics.motion.JointTarget;

import java.util.List;
import java.util.Optional;

public interface JointControl extends Behaviour {
    Optional<JointData> getJointData(String jointId);

    List<JointData> getJointsData();

    boolean isJointPresent(String jointId);

    Joint getJoint(String jointId);

    List<Joint> getJoints();

    void setJointPosition(JointTarget position);

    void setJointPositions(List<JointTarget> jointPositions);

    void runMotion(String motionId);
}
