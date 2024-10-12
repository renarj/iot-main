package com.oberasoftware.robo.maximus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;
import com.oberasoftware.robo.maximus.motion.JointControlImpl;
import com.oberasoftware.robo.maximus.motion.MotionEngineImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class JointBasedRobotImpl implements JointBasedRobot {
    private static final Logger LOG = getLogger( JointBasedRobotImpl.class );

    private final String name;
    private final String controllerId;

    private final List<JointChain> jointSets;

    private final RobotHardware robot;
    private final List<Sensor> sensors;

    private final List<Behaviour> behaviours = new ArrayList<>();
    private final List<Behaviour> registeredBehaviours = new ArrayList<>();

    public JointBasedRobotImpl(String controllerId, RobotHardware robot, String name, List<JointChain> jointChains, List<Sensor> sensors, List<Behaviour> behaviours) {
        this.controllerId = controllerId;
        this.robot = robot;
        this.name = name;
        this.registeredBehaviours.addAll(behaviours);

        jointSets = Lists.newArrayList(jointChains);
        this.sensors = sensors;
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
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
    public RobotHardware getRobotCore() {
        return robot;
    }

    @Override
    @JsonIgnore
    public JointControl getJointControl() {
        return getBehaviour(JointControl.class);
    }

    @Override
    public List<Joint> getJoints(boolean includeChildren) {
        if(includeChildren) {
            var b = ImmutableList.<Joint>builder();
            jointSets.forEach(j -> b.addAll(j.getJoints(true)));

            return b.build();
        }
        return getJoints();
    }

    @Override
    public List<Joint> getJoints() {
        return Lists.newArrayList();
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
    public String getControllerId() {
        return controllerId;
    }
}
