package com.oberasoftware.iot.core.robotics.humanoid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.humanoid.components.ChainSet;
import com.oberasoftware.iot.core.robotics.humanoid.components.Head;
import com.oberasoftware.iot.core.robotics.humanoid.components.Legs;
import com.oberasoftware.iot.core.robotics.humanoid.components.Torso;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;

import java.util.List;
import java.util.Optional;

public interface HumanoidRobot extends BehaviouralRobot, ChainSet {

    Optional<ChainSet> getChainSet(String name);

    List<ChainSet> getChainSets();

    List<ChainSet> getChainSets(boolean includeChildren);

    Robot getRobotCore();

    JointControl getJointControl();

    List<Sensor> getSensors();

    Sensor getSensor(String name);

    @JsonIgnore
    Head getHead();

    @JsonIgnore
    Torso getTorso();

    @JsonIgnore
    Legs getLegs();
}
