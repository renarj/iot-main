package com.oberasoftware.iot.core.robotics.humanoid;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;

import java.util.List;

public interface ConfigurableRobot extends Robot, JointChain {

    RobotHardware getRobotCore();

    JointControl getJointControl();

    List<Sensor> getSensors();

    Sensor getSensor(String name);

    List<Wheel> getWheels();
}
