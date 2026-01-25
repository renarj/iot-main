package com.oberasoftware.robodog.model;

import com.oberasoftware.robodog.odrive.device.ODriveDevice;
import com.oberasoftware.robodog.odrive.device.ODriveManager;

import java.util.ArrayList;
import java.util.List;

public class RobotDogBuilder {
    private final ODriveManager odriveManager;
    private final List<Leg> legs = new ArrayList<>();

    private double thighLenght = 0.26; // Default 10cm
    private double shinLength = 0.275; // Default 10cm

    private RobotDogBuilder(ODriveManager odriveManager) {
        this.odriveManager = odriveManager;
    }

    public static RobotDogBuilder create(ODriveManager odriveManager) {
        return new RobotDogBuilder(odriveManager);
    }

    public RobotDogBuilder withLegDimensions(double thighLenght, double shinLength) {
        this.thighLenght = thighLenght;
        this.shinLength = shinLength;
        return this;
    }

    public RobotDogBuilder addLeg(String legName, JointConfig knee, JointConfig hipPitch, JointConfig hipRoll) {
        Joint kneeJoint = createJoint(legName + "Knee", knee);
        Joint hipPitchJoint = createJoint(legName + "HipPitch", hipPitch);
        Joint hipRollJoint = createJoint(legName + "HipRoll", hipRoll);

        legs.add(new LegImpl(legName, kneeJoint, hipPitchJoint, hipRollJoint, thighLenght, shinLength));
        return this;
    }

    private Joint createJoint(String name, JointConfig config) {
        ODriveDevice drive = odriveManager.get(config.nodeId);
        if (drive == null) {
            drive = odriveManager.register(config.nodeId, name);
        }
        return new ODriveJoint(name, drive, config.reversed, config.minAngle, config.maxAngle, config.centerAngle);
    }

    public RobotDog build() {
        return new RobotDogImpl(legs);
    }

    public static class JointConfig {
        private final int nodeId;
        private final boolean reversed;
        private final double minAngle;
        private final double maxAngle;
        private final double centerAngle;

        public JointConfig(int nodeId, boolean reversed, double minAngle, double maxAngle, double centerAngle) {
            this.nodeId = nodeId;
            this.reversed = reversed;
            this.minAngle = minAngle;
            this.maxAngle = maxAngle;
            this.centerAngle = centerAngle;
        }

        public static JointConfig of(int nodeId, boolean reversed, double minAngle, double maxAngle, double centerAngle) {
            return new JointConfig(nodeId, reversed, minAngle, maxAngle, centerAngle);
        }
        
        public static JointConfig of(int nodeId) {
            return new JointConfig(nodeId, false, -360, 360, 0);
        }
    }
}
