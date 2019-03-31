package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.humanoid.*;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.maximus.impl.*;

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
                new JointImpl(pitchId, name + PITCH, name + PITCH),
                new JointImpl(yawId, name + YAW, name + YAW));
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

        public static LegBuilder create(String legName) {
            return new LegBuilder(legName);
        }

        public LegBuilder ankle(String ankleName, String servoXId, String servoYId) {
            ankle = new AnkleImpl(ankleName,
                    new JointImpl(servoXId, ankleName + "x", "ankle-x"),
                    new JointImpl(servoYId, ankleName + "y", "ankle-y"));

            return this;
        }

        public LegBuilder knee(String name, String kneeId) {
            knee = new JointImpl(kneeId, name, "knee");
            return this;
        }

        public LegBuilder hip(String hipName, String xId, String yId, String zId) {
            hip = new HipImpl(hipName,
                    new JointImpl(xId, hipName + "x", "hip-x"),
                    new JointImpl(yId, hipName + "y", "hip-y"),
                    new JointImpl(zId, hipName + "z", "hip-z"));
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

        public static ArmBuilder create(String armName) {
            return new ArmBuilder(armName);
        }

        public ArmBuilder shoulder(String name, String xId, String yId, String zId) {
            shoulder = new ShoulderImpl(name,
                    new JointImpl(xId, name + "x", "shoulder-x"),
                    new JointImpl(yId, name + "y", "shoulder-y"),
                    new JointImpl(zId, name + "z", "shoulder-z"));
            return this;
        }

        public ArmBuilder elbow(String name, String elbowId) {
            elbow = new JointImpl(elbowId, name, "elbow");
            return this;
        }

        public ArmBuilder hand(String name, String handId) {
            hand = new JointImpl(handId, name, "hand");
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

    public static class BuildException extends RoboException {
        public BuildException(String message) {
            super(message);
        }
    }
}
