package com.oberasoftware.robo.maximus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.*;
import com.oberasoftware.robo.api.sensors.Sensor;
import com.oberasoftware.robo.maximus.motion.MotionControlImpl;
import com.oberasoftware.robo.maximus.motion.MotionEngine;
import com.oberasoftware.robo.maximus.motion.MotionEngineImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HumanoidRobotImpl implements HumanoidRobot {

    private final String name;

    private final List<ChainSet> chainSets;

    private final Robot robot;
    private final Head head;
    private final Torso torso;
    private final Legs legs;

    private final List<Sensor> sensors;

    private List<Behaviour> behaviours = new ArrayList<>();

    public HumanoidRobotImpl(Robot robot, String name, Legs legs, Torso torso, Head head, List<Sensor> sensors) {
        this.robot = robot;
        this.name = name;
        this.legs = legs;
        this.torso = torso;
        this.head = head;

        chainSets = ImmutableList.<ChainSet>builder()
                .add(legs)
                .add(torso)
                .add(head)
                .build();
        this.sensors = sensors;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        MotionEngine motionEngine = new MotionEngineImpl();
        motionEngine.initialize(behaviouralRobot, robotCore);
        behaviours.add(motionEngine);

        MotionControl motionControl = new MotionControlImpl(getJoints(true));
        motionControl.initialize(behaviouralRobot, robotCore);
        behaviours.add(motionControl);

        ServoListener listener = new ServoListener(motionControl);
        robotCore.listen(listener);

        this.sensors.forEach(s -> {
            s.activate(robotCore);
        });
    }

    @Override
    public <T extends Behaviour> T getBehaviour(Class<T> behaviourClass) {
        Optional<Behaviour> o = behaviours.stream()
                .filter(behaviourClass::isInstance)
                .findFirst();
        if(o.isPresent()) {
            return (T)o.get();
        } else {
            return null;
        }
    }

    @Override
    @JsonIgnore
    public Robot getRobotCore() {
        return robot;
    }

    @Override
    @JsonIgnore
    public MotionControl getMotionControl() {
        return getBehaviour(MotionControl.class);
    }

    @Override
    public Optional<ChainSet> getChainSet(String name) {
        return chainSets.stream()
                .filter(c -> c.getName()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<ChainSet> getChainSets() {
        return chainSets;
    }

    @Override
    public List<ChainSet> getChainSets(boolean includeChildren) {
        return chainSets;
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        if(includeChildren) {
            return ImmutableList.<Joint>builder()
                    .addAll(legs.getJoints(true))
                    .addAll(torso.getJoints(true))
                    .addAll(head.getJoints(true))
                    .build();
        }
        return getJoints();
    }

    @Override
    public List<Joint> getJoints() {
        return Lists.newArrayList();
    }

    @Override
    @JsonIgnore
    public List<JointChain> getJointChains() {
        return chainSets.stream()
                .flatMap(c -> c.getJointChains().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Sensor> getSensors() {
        return sensors;
    }

    @Override
    public Sensor getSensor(String name) {
        return sensors.stream().filter(s -> s.getName()
                .equalsIgnoreCase(name)).findFirst().orElseThrow();
    }

    @Override
    public Head getHead() {
        return head;
    }

    @Override
    public Torso getTorso() {
        return torso;
    }

    @Override
    public Legs getLegs() {
        return legs;
    }

    @Override
    public String getName() {
        return getRobotId();
    }

    @Override
    public String getRobotId() {
        return name;
    }

    @Override
    public List<Behaviour> getBehaviours() {
        return null;
    }
}
