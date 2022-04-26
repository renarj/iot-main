package com.oberasoftware.robo.api.humanoid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.humanoid.components.ChainSet;
import com.oberasoftware.robo.api.humanoid.components.Head;
import com.oberasoftware.robo.api.humanoid.components.Legs;
import com.oberasoftware.robo.api.humanoid.components.Torso;
import com.oberasoftware.robo.api.sensors.Sensor;

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
