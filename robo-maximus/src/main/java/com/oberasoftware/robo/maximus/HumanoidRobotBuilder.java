package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;

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
}
