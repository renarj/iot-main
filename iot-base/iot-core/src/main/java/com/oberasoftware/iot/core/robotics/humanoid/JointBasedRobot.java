package com.oberasoftware.iot.core.robotics.humanoid;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;

import java.util.List;
import java.util.Optional;

public interface JointBasedRobot extends Robot, JointChain {

    RobotHardware getRobotCore();

    JointControl getJointControl();

    List<Sensor> getSensors();

    Sensor getSensor(String name);
}
