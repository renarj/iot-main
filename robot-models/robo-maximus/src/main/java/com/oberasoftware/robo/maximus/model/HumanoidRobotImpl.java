package com.oberasoftware.robo.maximus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.model.storage.impl.AttributeType;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.humanoid.HumanoidRobot;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.components.ChainSet;
import com.oberasoftware.iot.core.robotics.humanoid.components.Head;
import com.oberasoftware.iot.core.robotics.humanoid.components.Legs;
import com.oberasoftware.iot.core.robotics.humanoid.components.Torso;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;
import com.oberasoftware.robo.maximus.motion.JointControlImpl;
import com.oberasoftware.robo.maximus.motion.MotionEngineImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class HumanoidRobotImpl implements HumanoidRobot {
    private static final Logger LOG = getLogger( HumanoidRobotImpl.class );

    private final String name;

    private final List<ChainSet> chainSets;

    private final Robot robot;
    private final Head head;
    private final Torso torso;
    private final Legs legs;

    private final List<Sensor> sensors;

    private final List<Behaviour> behaviours = new ArrayList<>();
    private final List<Behaviour> registeredBehaviours = new ArrayList<>();

    public HumanoidRobotImpl(Robot robot, String name, Legs legs, Torso torso, Head head, List<Sensor> sensors, List<Behaviour> behaviours) {
        this.robot = robot;
        this.name = name;
        this.legs = legs;
        this.torso = torso;
        this.head = head;

        this.registeredBehaviours.addAll(behaviours);

        chainSets = ImmutableList.<ChainSet>builder()
                .add(legs)
                .add(torso)
                .add(head)
                .build();
        this.sensors = sensors;
    }

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        List<Joint> joints = getJoints(true);

        MotionEngineImpl motionEngine = new MotionEngineImpl(joints);
        motionEngine.initialize(behaviouralRobot, robotCore);
        behaviours.add(motionEngine);

        JointControl motionControl = new JointControlImpl(getJoints(true));
        motionControl.initialize(behaviouralRobot, robotCore);
        behaviours.add(motionControl);

        registeredBehaviours.forEach(b -> {
            LOG.info("Registering and initializing behaviour controller: {}", b);
            b.initialize(behaviouralRobot, robotCore);
            behaviours.add(b);
        });

        ServoListener listener = new ServoListener(motionControl);
        robotCore.listen(listener);

        this.sensors.forEach(s -> {
            s.activate(robotCore);
        });

        registerRemoteCapabilities(robotCore, motionControl);
    }

    private void registerRemoteCapabilities(Robot robotCore, JointControl motionControl) {
        if(robotCore.isRemote()) {
            var controllerId = robot.getName();
            robotCore.getRemoteDriver().registerThing(new IotThingImpl(robot.getName(), "joints", "joints", "JointControl", null, new HashMap<>()));
            robotCore.getRemoteDriver().registerThing(new IotThingImpl(robot.getName(), "servos", "servos", "ServoDriver", null, new HashMap<>()));

            motionControl.getJoints().forEach(j -> {
                var joint = new IotThingImpl(robot.getName(), "joints." + j.getID(), j.getName(), "joints", "joints", new HashMap<>());
                joint.addAttribute("degrees", AttributeType.DEGREES);
                joint.addAttribute("position", AttributeType.ABS_POSITION);
                robotCore.getRemoteDriver().registerThing(joint);
            });

            robotCore.getServoDriver().getServos().forEach(s -> {
                var builder = ThingBuilder.create("servos." + s.getId(), controllerId)
                        .parent("servos")
                        .friendlyName("servo-" + s.getId())
                        .plugin("servos");
                s.getData().getKeys().forEach(k -> builder.addAttribute(k.name(), AttributeType.LABEL));
                robotCore.getRemoteDriver().registerThing(builder.build());
            });

//            this.sensors.forEach(s -> {
//
//            });
        }
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
    public JointControl getJointControl() {
        return getBehaviour(JointControl.class);
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
