package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.humanoid.*;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.maximus.model.*;

public class HumanoidRobotBuilder {

    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";

    private final String name;
    private final Robot robot;

    private Legs legs;
    private Torso torso;
    private Head head;

    public HumanoidRobotBuilder(Robot robot, String name) {
        this.robot = robot;
        this.name = name;
    }

    public static HumanoidRobotBuilder create(Robot robot, String name) {
        return new HumanoidRobotBuilder(robot, name);
    }

    public HumanoidRobotBuilder legs(LegBuilder leftLeg, LegBuilder rightLeg) {
        legs = new LegsImpl(leftLeg.build(), rightLeg.build());
        return this;
    }

    public HumanoidRobotBuilder torso(ArmBuilder leftArm, ArmBuilder rightArm) {
        torso = new TorsoImpl(leftArm.build(), rightArm.build());
        return this;
    }

    public HumanoidRobotBuilder head(String name, String pitchId, String yawId) {
        head = new HeadImpl(name,
                new JointImpl(pitchId, name + PITCH, name + PITCH, false),
                new JointImpl(yawId, name + YAW, name + YAW, false));
        return this;
    }

    public HumanoidRobot build() {
        if(legs != null && torso != null) {
            HumanoidRobot humanoidRobot = new HumanoidRobotImpl(robot, name, legs, torso, head);
            humanoidRobot.initialize(humanoidRobot, robot);

            return humanoidRobot;
        } else {
            throw new BuildException("Cannot build a robot without legs or arms!!!!");
        }
    }

    public static class LegBuilder {
        private final String legName;

        private Hip hip;
        private Joint knee;
        private Ankle ankle;

        private LegBuilder(String legName) {
            this.legName = legName;
        }

        public static LegBuilder createLeg(String legName) {
            return new LegBuilder(legName);
        }

        public LegBuilder ankle(String ankleName, JointBuilder x, JointBuilder y) {
            ankle = new AnkleImpl(ankleName,
                    x.type("ankle-x").build(),
                    y.type("ankle-y").build());

            return this;
        }

        public LegBuilder knee(JointBuilder jointBuilder) {
            knee = jointBuilder.type("knee").build();
            return this;
        }

        public LegBuilder hip(String hipName, JointBuilder x, JointBuilder y, JointBuilder z) {
            hip = new HipImpl(hipName,
                    x.type("hip-x").build(),
                    y.type("hip-y").build(),
                    z.type("hip-z").build());
            return this;
        }

        private Leg build() throws RoboException {
            if(hip != null && knee != null && ankle != null) {
                return new LegImpl(legName, hip, knee, ankle);
            } else {
                throw new BuildException("Could not create robot, missing hip, knee or ankle");
            }
        }
    }

    public static class ArmBuilder {
        private final String armName;

        private Shoulder shoulder;
        private Joint elbow;
        private Joint hand;

        public ArmBuilder(String armName) {
            this.armName = armName;
        }

        public static ArmBuilder createArm(String armName) {
            return new ArmBuilder(armName);
        }

        public ArmBuilder shoulder(String name, String xId, String yId, String zId) {
            shoulder = new ShoulderImpl(name,
                    new JointImpl(xId, name + "x", "shoulder-x", false),
                    new JointImpl(yId, name + "y", "shoulder-y", false),
                    new JointImpl(zId, name + "z", "shoulder-z", false));
            return this;
        }

        public ArmBuilder elbow(JointBuilder jointBuilder) {
            elbow = jointBuilder.type("elbow").build();
            return this;
        }

        public ArmBuilder hand(String name, String handId) {
            hand = new JointImpl(handId, name, "hand", false);
            return this;
        }

        public Arm build() {
            if(shoulder != null && elbow != null && hand != null) {
                return new ArmImpl(armName, shoulder, elbow, hand);
            } else {
                throw new BuildException("Could not create robot, missing shoulder, elbow or hand");
            }
        }
    }

    public static class JointBuilder {
        private final String id;
        private final String name;

        private final boolean inverted;

        private String type;
        private Integer min;
        private Integer max;

        public JointBuilder(String id, String name, boolean inverted) {
            this.id = id;
            this.name = name;
            this.inverted = inverted;
        }

        public static JointBuilder create(String id, String name) {
            return new JointBuilder(id, name, false);
        }

        public static JointBuilder create(String id, String name, boolean inverted) {
            return new JointBuilder(id, name, inverted);
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

        public Joint build() {
            if(min != null && max != null) {
                return new JointImpl(id, name, type, min, max, inverted);
            } else {
                return new JointImpl(id, name, type, inverted);
            }
        }
    }

    public static class BuildException extends RoboException {
        public BuildException(String message) {
            super(message);
        }
    }
}
