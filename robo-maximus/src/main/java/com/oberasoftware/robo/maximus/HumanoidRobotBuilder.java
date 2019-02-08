package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.api.behavioural.humanoid.*;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.maximus.impl.LegImpl;

public class HumanoidRobotBuilder {
    public static HumanoidRobotBuilder create(String maximus) {
        return new HumanoidRobotBuilder();
    }

    public HumanoidRobotBuilder legs(LegBuilder leftLeg, LegBuilder rightLeg) {
        return this;
    }

    public HumanoidRobotBuilder torso(ArmBuilder leftArm, ArmBuilder rightArm) {
        return this;
    }

    public HumanoidRobotBuilder head(String pitchId, String yawId) {
        return this;
    }

    public HumanoidRobot build() {
        return null;
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

        public LegBuilder ankle(String servoXId, String servoYId) {


            return this;
        }

        public LegBuilder knee(String kneeId) {
            return this;
        }

        public LegBuilder hip(String xId, String yId, String zId) {
            return this;
        }

        private Leg build() throws RoboException {
            if(hip != null && knee != null && ankle != null) {
                return new LegImpl(legName, null, null, null);
            } else {
                throw new BuildException("Could not create robot, missing hip, knee or ankle");
            }
        }
    }

    public static class ArmBuilder {
        private final String armName;

        public ArmBuilder(String armName) {
            this.armName = armName;
        }

        public static ArmBuilder create(String armName) {
            return new ArmBuilder(armName);
        }

        public ArmBuilder shoulder(String xId, String yId, String zId) {
            return this;
        }

        public ArmBuilder elbow(String elbowId) {
            return this;
        }

        public ArmBuilder hand(String handId) {
            return this;
        }
    }

    public static class BuildException extends RoboException {
        public BuildException(String message) {
            super(message);
        }
    }
}
