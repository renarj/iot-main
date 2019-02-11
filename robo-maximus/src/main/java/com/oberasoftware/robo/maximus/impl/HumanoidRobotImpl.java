package com.oberasoftware.robo.maximus.impl;

import com.google.common.collect.ImmutableList;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.humanoid.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HumanoidRobotImpl implements HumanoidRobot {

    private final String name;

    private final List<ChainSet> chainSets;

    public HumanoidRobotImpl(String name, Legs legs, Torso torso, Head head) {
        this.name = name;

        chainSets = ImmutableList.<ChainSet>builder()
                .add(legs)
                .add(torso)
                .add(head)
                .build();
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
    public List<Joint> getJoints() {
        return chainSets.stream()
                .flatMap(c -> c.getJoints().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<JointChain> getChains() {
        return chainSets.stream()
                .flatMap(c -> c.getChains().stream())
                .collect(Collectors.toList());
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
