package com.oberasoftware.iot.core.robotics.behavioural;


import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.wheel.Wheel;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;

import java.util.List;

/**
 * @author renarj
 */
public interface Robot extends JointChain {
    String getRobotId();

    String getControllerId();

    List<Behaviour> getBehaviours();

    <T extends Behaviour> T getBehaviour(Class<T> behaviourClass);

    RobotHardware getRobotCore();

    JointControl getJointControl();

    List<Sensor> getSensors();

    Sensor getSensor(String name);

    List<Wheel> getWheels();

    Wheel getWheels(String name);
}
