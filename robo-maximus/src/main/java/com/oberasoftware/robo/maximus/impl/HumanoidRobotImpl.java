package com.oberasoftware.robo.maximus.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.humanoid.*;

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

    private MotionControl motionControl;

    public HumanoidRobotImpl(Robot robot, String name, Legs legs, Torso torso, Head head) {
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
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        motionControl = new MotionControlImpl();
        motionControl.initialize(behaviouralRobot, robotCore);
    }

    @Override
    public Robot getRobotCore() {
        return robot;
    }

    @Override
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

    @Override
    public <T extends Behaviour> T getBehaviour(Class<T> behaviourClass) {
        return null;
    }
}
