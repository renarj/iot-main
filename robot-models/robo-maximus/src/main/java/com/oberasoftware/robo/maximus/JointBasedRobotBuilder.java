package com.oberasoftware.robo.maximus;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import com.oberasoftware.iot.core.robotics.humanoid.components.*;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointChain;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;
import com.oberasoftware.robo.maximus.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.oberasoftware.iot.core.robotics.humanoid.components.ComponentNames.*;

public class JointBasedRobotBuilder implements RobotBuilder {

    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";


    private String name;
    private String controllerId;

    private final Map<String, JointChain> jointChains = new HashMap<>();

    private final List<Sensor> sensors = new ArrayList<>();
    private final List<Behaviour> behaviours = new ArrayList<>();

    private LegBuilder leftLeg;
    private LegBuilder rightLeg;
    private ArmBuilder leftArm;
    private ArmBuilder rightArm;

    private String headName;
    private JointBuilder headPitch;
    private JointBuilder headRoll;

    public JointBasedRobotBuilder(String controllerId, String name) {
        this.name = name;
        this.controllerId = controllerId;
    }

    public JointBasedRobotBuilder() {
    }

    public JointBasedRobotBuilder name(String controllerId, String name) {
        this.name = name;
        this.controllerId = controllerId;
        return this;
    }

    public static JointBasedRobotBuilder create(String controllerId, String name) {
        return new JointBasedRobotBuilder(controllerId, name);
    }

    public JointBasedRobotBuilder legs(LegBuilder leftLeg, LegBuilder rightLeg) {
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        return this;
    }

    public JointBasedRobotBuilder torso(ArmBuilder leftArm, ArmBuilder rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        return this;
    }

    public JointBasedRobotBuilder head(String name, JointBuilder pitchBuilder, JointBuilder rollBuilder) {
        this.headName = name;
        this.headPitch = pitchBuilder;
        this.headRoll = rollBuilder;

        return this;
    }

    public JointBasedRobotBuilder joints(String setName, List<JointBuilder> jbs) {
        var chain = new JointChain() {
            @Override
            public List<Joint> getJoints(boolean includeChildren) {
                return jbs.stream().map(jb -> jb.build(name, controllerId)).collect(Collectors.toList());
            }

            @Override
            public String getName() {
                return setName;
            }
        };
        jointChains.put(setName, chain);
        return this;
    }

    public JointBasedRobotBuilder sensor(Sensor sensor) {
        this.sensors.add(sensor);
        return this;
    }

    public JointBasedRobotBuilder behaviourController(Behaviour behaviour) {
        behaviours.add(behaviour);
        return this;
    }

    public JointBasedRobot build(RobotHardware hardware) {
        var combinedJoints = Lists.newArrayList(jointChains.values());
        if(leftLeg != null && rightLeg != null) {
            var legs = new LegsImpl(leftLeg.build(name, controllerId), rightLeg.build(name, controllerId));
            combinedJoints.add(legs);
        }
        if(leftArm != null && rightArm != null) {
            var torso = new TorsoImpl(leftArm.build(name, controllerId), rightArm.build(name, controllerId));
            combinedJoints.add(torso);
        }
        if(headPitch != null && headRoll != null) {
            var head = new HeadImpl(name, headPitch.build(name, controllerId), headRoll.build(name, controllerId));
            combinedJoints.add(head);
        }
        JointBasedRobot jointBasedRobot = new JointBasedRobotImpl(controllerId, hardware, name, combinedJoints, sensors, behaviours);
        jointBasedRobot.initialize(jointBasedRobot, hardware);

        return jointBasedRobot;
    }

    public static class LegBuilder {
        private final String legName;

        private String ankleName;
        private String hipName;

        private JointBuilder kneeBuilder;
        private JointBuilder ankleXBuilder;
        private JointBuilder ankleYBuilder;

        private JointBuilder hipXBuilder;
        private JointBuilder hipYBuilder;
        private JointBuilder hipZBuilder;

        private LegBuilder(String legName) {
            this.legName = legName;
        }

        public static LegBuilder createLeg(String legName) {
            return new LegBuilder(legName);
        }

        public LegBuilder ankle(String ankleName, JointBuilder x, JointBuilder y) {
            this.ankleName = ankleName;
            this.ankleXBuilder = x;
            this.ankleYBuilder = y;
            return this;
        }

        public LegBuilder knee(JointBuilder jointBuilder) {
            this.kneeBuilder = jointBuilder;
            return this;
        }

        public LegBuilder hip(String hipName, JointBuilder x, JointBuilder y, JointBuilder z) {
            this.hipName = hipName;
            this.hipXBuilder = x;
            this.hipYBuilder = y;
            this.hipZBuilder = z;
            return this;
        }

        private Leg build(String robotId, String controllerId) throws RoboException {
            var a = new AnkleImpl(ankleName, ankleXBuilder.build(robotId, controllerId), ankleYBuilder.build(robotId, controllerId));
            var h = new HipImpl(hipName, hipXBuilder.build(robotId, controllerId), hipYBuilder.build(robotId, controllerId), hipZBuilder.build(robotId, controllerId));

            return new LegImpl(legName, h, kneeBuilder.build(robotId, controllerId), a);
        }
    }

    public static class ArmBuilder {
        private final String armName;

        private Shoulder shoulder;
        private String shoulderName;
        private JointBuilder shoulderX;
        private JointBuilder shoulderY;

        private JointBuilder elbow;

        private JointBuilder hand;

        public ArmBuilder(String armName) {
            this.armName = armName;
        }

        public static ArmBuilder createArm(String armName) {
            return new ArmBuilder(armName);
        }

        public ArmBuilder shoulder(String name, JointBuilder x, JointBuilder y) {
            this.shoulderName = name;
            this.shoulderX = x;
            this.shoulderY = y;
            return this;
        }

        public ArmBuilder elbow(JointBuilder jointBuilder) {
            elbow = jointBuilder.type(ELBOW);

            return this;
        }

        public ArmBuilder hand(JointBuilder jointBuilder) {
            hand = jointBuilder.type(HAND);
            return this;
        }

        public Arm build(String robotId, String controllerId) {
            var shoulder = new ShoulderImpl(shoulderName, shoulderX.build(robotId, controllerId), shoulderY.build(robotId, controllerId));

            return new ArmImpl(armName, shoulder, elbow.build(robotId, controllerId), hand.build(robotId, controllerId));
        }
    }

    public static class JointBuilder {
        private final String thingId;
        private final String name;
        private final String servoId;

        private final boolean inverted;

        private String type;
        private Integer min;
        private Integer max;

        public JointBuilder(String thingId, String servoId, String name, boolean inverted) {
            this.thingId = thingId;
            this.servoId = servoId;
            this.name = name;
            this.inverted = inverted;
        }

        public static JointBuilder create(String thingId, String servoId, String name) {
            return new JointBuilder(thingId, servoId, name, false);
        }

        /**
         * Legacy API call, assuming servo and joint id are equal
         * @param thingId
         * @param name
         * @return
         */
        public static JointBuilder create(String thingId, String name) {
            return new JointBuilder(thingId, thingId, name, false);
        }

        public static JointBuilder create(String thingId, String servoId, String name, boolean inverted) {
            return new JointBuilder(thingId, servoId, name, inverted);
        }
        public static JointBuilder create(String thingId, String name, boolean inverted) {
            return new JointBuilder(thingId, thingId, name, inverted);
        }
        public JointBuilder type(String type) {
            this.type = type;
            return this;
        }

        public JointBuilder min(int min) {
            this.min = min;
            return this;
        }

        public JointBuilder max(int max) {
            this.max = max;
            return this;
        }

        public Joint build(String robotId, String controllerId) {
            if(min != null && max != null) {
                return new JointImpl(robotId, controllerId, thingId, servoId, name, type, min, max, inverted);
            } else {
                return new JointImpl(robotId, controllerId, thingId, servoId, name, type, inverted);
            }
        }
    }
}
